package com.umniyali.app.data.repository

import com.umniyali.app.data.local.SettingsDao
import com.umniyali.app.data.local.entity.SettingsEntity
import com.umniyali.app.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(private val settingsDao: SettingsDao) {

    val settingsFlow: Flow<UserSettings> = settingsDao.getSettingsFlow().map { it?.toDomain() ?: UserSettings() }

    suspend fun getSettings(): UserSettings {
        return settingsDao.getSettings()?.toDomain() ?: UserSettings()
    }

    suspend fun saveSettings(settings: UserSettings) {
        settingsDao.saveSettings(settings.toEntity())
    }

    private fun SettingsEntity.toDomain() = UserSettings(
        assistantName = assistantName,
        userName = userName,
        character = character,
        language = language,
        ttsVoice = ttsVoice,
        serverUrl = serverUrl
    )

    private fun UserSettings.toEntity() = SettingsEntity(
        assistantName = assistantName,
        userName = userName,
        character = character,
        language = language,
        ttsVoice = ttsVoice,
        serverUrl = serverUrl
    )
}
