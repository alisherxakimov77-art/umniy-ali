package com.umniyali.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.umniyali.app.R
import com.umniyali.app.ui.theme.JarvisBlue
import com.umniyali.app.ui.theme.JarvisCyan
import com.umniyali.app.ui.theme.TextMuted
import com.umniyali.app.ui.theme.TextPrimary

@Composable
fun JarvisHeader(assistantName: String, isOnline: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        JarvisBlue.copy(alpha = 0.15f),
                        androidx.compose.ui.graphics.Color.Transparent
                    )
                )
            )
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(JarvisCyan, JarvisBlue)
                    ),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "AI",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color.Black
            )
        }

        Text(
            text = assistantName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.padding(top = 12.dp)
        )

        Text(
            text = if (isOnline) stringResource(R.string.online_status) else stringResource(R.string.offline_status),
            style = MaterialTheme.typography.bodyMedium,
            color = if (isOnline) JarvisBlue else TextMuted,
            modifier = Modifier.alpha(0.9f)
        )
    }
}
