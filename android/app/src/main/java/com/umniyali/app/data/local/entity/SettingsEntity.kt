package com.umniyali.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = 1,
    val assistantName: String = "Умный Али",
    val userName: String = "",
    val character: String = "jarvis",
    val language: String = "ru",
    val ttsVoice: String = "ru-RU",
    val serverUrl: String = "https://your-server.example.com/"
)
