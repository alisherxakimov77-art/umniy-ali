package com.umniyali.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umniyali.app.data.repository.SettingsRepository
import com.umniyali.app.domain.model.UserSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _settings = MutableStateFlow(UserSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.settingsFlow.collect {
                _settings.value = it
            }
        }
    }

    fun updateAssistantName(name: String) {
        _settings.value = _settings.value.copy(assistantName = name)
    }

    fun updateUserName(name: String) {
        _settings.value = _settings.value.copy(userName = name)
    }

    fun updateCharacter(character: String) {
        _settings.value = _settings.value.copy(character = character)
    }

    fun updateLanguage(language: String) {
        _settings.value = _settings.value.copy(language = language)
    }

    fun updateTtsVoice(voice: String) {
        _settings.value = _settings.value.copy(ttsVoice = voice)
    }

    fun updateServerUrl(url: String) {
        _settings.value = _settings.value.copy(serverUrl = url)
    }

    fun save() {
        viewModelScope.launch {
            settingsRepository.saveSettings(_settings.value)
        }
    }
}
