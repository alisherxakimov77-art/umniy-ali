package com.umniyali.app.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.umniyali.app.R
import com.umniyali.app.ui.theme.DarkBackground
import com.umniyali.app.ui.theme.DarkSurfaceElevated
import com.umniyali.app.ui.theme.JarvisBlue
import com.umniyali.app.ui.theme.TextPrimary
import com.umniyali.app.ui.theme.TextSecondary
import com.umniyali.app.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.title_settings),
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.save()
                        onBack()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsCard(title = stringResource(R.string.profile)) {
                SettingsTextField(
                    value = settings.assistantName,
                    onValueChange = viewModel::updateAssistantName,
                    label = stringResource(R.string.assistant_name)
                )
                SettingsTextField(
                    value = settings.userName,
                    onValueChange = viewModel::updateUserName,
                    label = stringResource(R.string.user_name),
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            SettingsCard(title = stringResource(R.string.character_and_language)) {
                DropdownSelector(
                    label = stringResource(R.string.character),
                    options = listOf(
                        "jarvis" to "Джарвис",
                        "friendly" to "Дружелюбный",
                        "business" to "Деловой"
                    ),
                    selected = settings.character,
                    onSelected = viewModel::updateCharacter
                )

                DropdownSelector(
                    label = stringResource(R.string.language),
                    options = listOf(
                        "ru" to "Русский",
                        "uz" to "O'zbek",
                        "en" to "English"
                    ),
                    selected = settings.language,
                    onSelected = viewModel::updateLanguage,
                    modifier = Modifier.padding(top = 12.dp)
                )

                DropdownSelector(
                    label = stringResource(R.string.voice),
                    options = listOf(
                        "ru-RU" to "Русский",
                        "uz-UZ" to "O'zbek",
                        "en-US" to "English"
                    ),
                    selected = settings.ttsVoice,
                    onSelected = viewModel::updateTtsVoice,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            SettingsCard(title = stringResource(R.string.server)) {
                SettingsTextField(
                    value = settings.serverUrl,
                    onValueChange = viewModel::updateServerUrl,
                    label = "Server URL"
                )
            }

            Text(
                text = stringResource(R.string.version),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 24.dp)
            )
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(DarkSurfaceElevated)
            .padding(16.dp)
            .animateContentSize()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = JarvisBlue,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        content()
    }
}

@Composable
private fun SettingsTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = JarvisBlue,
            unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
            focusedLabelColor = JarvisBlue,
            unfocusedLabelColor = TextSecondary,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedContainerColor = DarkBackground.copy(alpha = 0.5f),
            unfocusedContainerColor = DarkBackground.copy(alpha = 0.5f)
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownSelector(
    label: String,
    options: List<Pair<String, String>>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = options.find { it.first == selected }?.second ?: selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = JarvisBlue,
                unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                focusedLabelColor = JarvisBlue,
                unfocusedLabelColor = TextSecondary,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = DarkBackground.copy(alpha = 0.5f),
                unfocusedContainerColor = DarkBackground.copy(alpha = 0.5f)
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(DarkBackground)
        ) {
            options.forEach { (key, title) ->
                DropdownMenuItem(
                    text = { Text(title, color = TextPrimary) },
                    onClick = {
                        onSelected(key)
                        expanded = false
                    }
                )
            }
        }
    }
}
