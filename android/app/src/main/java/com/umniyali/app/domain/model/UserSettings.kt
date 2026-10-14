package com.umniyali.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserSettings(
    val assistantName: String = "Умный Али",
    val userName: String = "",
    val character: String = "jarvis",
    val language: String = "ru",
    val ttsVoice: String = "ru-RU",
    val serverUrl: String = "https://your-server.example.com/"
)
