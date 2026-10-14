package com.umniyali.app.ui.screens

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.umniyali.app.R
import com.umniyali.app.service.SpeechRecognitionService
import com.umniyali.app.service.SpeechResult
import com.umniyali.app.service.TextToSpeechService
import com.umniyali.app.ui.components.JarvisHeader
import com.umniyali.app.ui.components.MessageBubble
import com.umniyali.app.ui.components.PulseMicButton
import com.umniyali.app.ui.components.TypingIndicator
import com.umniyali.app.ui.components.VoiceWaveform
import com.umniyali.app.ui.theme.DarkBackground
import com.umniyali.app.ui.theme.DarkSurfaceElevated
import com.umniyali.app.ui.theme.JarvisBlue
import com.umniyali.app.ui.theme.JarvisGlow
import com.umniyali.app.ui.theme.TextMuted
import com.umniyali.app.ui.theme.TextPrimary
import com.umniyali.app.ui.theme.TextSecondary
import com.umniyali.app.ui.viewmodel.ChatViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onNavigateToSettings: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val listState = rememberLazyListState()

    var inputText by remember { mutableStateOf("") }
    var pendingImageUri by remember { mutableStateOf<Uri?>(null) }

    val speechRecognizer = remember { SpeechRecognitionService(context) }
    val tts = remember { TextToSpeechService(context) }

    LaunchedEffect(settings.language, settings.ttsVoice) {
        tts.setLanguage(settings.ttsVoice)
    }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            tts.stop()
            speechRecognizer.startListening(settings.language)
            viewModel.setListening(true)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            pendingImageUri?.let { uri ->
                val file = uriToFile(context, uri)
                viewModel.analyzeFood(file) { spoken ->
                    tts.speak(spoken)
                }
                pendingImageUri = null
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            pendingImageUri = createImageUri(context)
            cameraLauncher.launch(pendingImageUri!!)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val file = uriToFile(context, it)
            viewModel.analyzeFood(file) { spoken ->
                tts.speak(spoken)
            }
        }
    }

    LaunchedEffect(speechRecognizer) {
        speechRecognizer.results.collect { result: SpeechResult ->
            when {
                result.text.isBlank() -> Unit
                result.isFinal -> {
                    inputText = ""
                    viewModel.setListening(false)
                    viewModel.sendMessage(result.text) { spoken ->
                        tts.speak(spoken)
                    }
                }
                else -> {
                    inputText = result.text
                }
            }
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                speechRecognizer.destroy()
                tts.shutdown()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = settings.assistantName,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = JarvisBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .navigationBarsPadding()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                item { JarvisHeader(assistantName = settings.assistantName, isOnline = true) }

                if (messages.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.empty_chat),
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextMuted,
                                modifier = Modifier.alpha(0.7f)
                            )
                        }
                    }
                }

                itemsIndexed(messages, key = { index, _ -> index }) { _, message ->
                    MessageBubble(
                        text = message.content,
                        isUser = message.role == "user"
                    )
                }

                if (isLoading && !isListening) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 12.dp, top = 4.dp, bottom = 4.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        DarkSurfaceElevated,
                                        RoundedCornerShape(20.dp)
                                    )
                            ) {
                                TypingIndicator()
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = isListening,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                VoiceWaveform(isListening = isListening)
            }

            pendingImageUri?.let { uri ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = uri,
                        contentDescription = "Selected food",
                        modifier = Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkSurfaceElevated)
                    )
                }
            }

            // Bottom input bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                androidx.compose.ui.graphics.Color.Transparent,
                                DarkBackground.copy(alpha = 0.9f),
                                DarkBackground
                            )
                        )
                    )
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Text input
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .background(DarkSurfaceElevated, RoundedCornerShape(28.dp))
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (inputText.isEmpty()) {
                            Text(
                                text = stringResource(R.string.hint_speak),
                                color = TextMuted,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        BasicTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            textStyle = TextStyle(
                                color = TextPrimary,
                                fontSize = MaterialTheme.typography.bodyLarge.fontSize
                            ),
                            singleLine = true,
                            cursorBrush = SolidColor(JarvisBlue),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Send button
                    AnimatedVisibility(
                        visible = inputText.isNotBlank() && !isLoading,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.sendMessage(inputText) { spoken ->
                                    tts.speak(spoken)
                                }
                                inputText = ""
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .background(JarvisBlue, CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Send,
                                contentDescription = stringResource(R.string.button_send),
                                tint = DarkBackground
                            )
                        }
                    }

                    // Camera
                    IconButton(
                        onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                        modifier = Modifier
                            .size(48.dp)
                            .background(DarkSurfaceElevated, CircleShape)
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = stringResource(R.string.button_camera),
                            tint = JarvisBlue
                        )
                    }

                    // Gallery
                    IconButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier
                            .size(48.dp)
                            .background(DarkSurfaceElevated, CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = stringResource(R.string.button_gallery),
                            tint = JarvisBlue
                        )
                    }

                    // Mic
                    PulseMicButton(
                        isListening = isListening,
                        onClick = {
                            if (isListening) {
                                speechRecognizer.stopListening()
                                viewModel.setListening(false)
                            } else {
                                audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                    )
                }
            }
        }
    }
}

private fun createImageUri(context: Context): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFile = File(context.cacheDir, "FOOD_${timeStamp}.jpg")
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
}

private fun uriToFile(context: Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri)
        ?: throw IllegalStateException("Cannot open image")
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val file = File(context.cacheDir, "FOOD_${timeStamp}.jpg")
    file.outputStream().use { output ->
        inputStream.copyTo(output)
    }
    return file
}
