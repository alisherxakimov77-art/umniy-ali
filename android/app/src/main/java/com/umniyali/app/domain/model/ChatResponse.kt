package com.umniyali.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatResponse(
    val type: String = "text",
    val text: String? = null,
    val action: Action? = null
)

@Serializable
data class Action(
    val action: String,
    val params: Map<String, String?> = emptyMap()
)
