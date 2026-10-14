package com.umniyali.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.umniyali.app.ui.theme.JarvisBlue
import com.umniyali.app.ui.theme.JarvisCyan

@Composable
fun VoiceWaveform(isListening: Boolean) {
    if (!isListening) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(7) { index ->
            val infiniteTransition = rememberInfiniteTransition(label = "wave_$index")
            val scaleY by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 400 + (index % 3) * 120,
                        easing = LinearEasing,
                        delayMillis = index * 80
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "wave_scale"
            )
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(40.dp)
                    .scale(scaleX = 1f, scaleY = scaleY)
                    .alpha(0.7f + 0.3f * scaleY)
                    .background(
                        if (index % 2 == 0) JarvisBlue else JarvisCyan,
                        RoundedCornerShape(3.dp)
                    )
            )
        }
    }
}
