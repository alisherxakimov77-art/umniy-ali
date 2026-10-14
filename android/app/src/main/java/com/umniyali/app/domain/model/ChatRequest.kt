package com.umniyali.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatRequest(
    val message: String,
    val settings: UserSettings,
    val history: List<ChatMessage> = emptyList()
)
