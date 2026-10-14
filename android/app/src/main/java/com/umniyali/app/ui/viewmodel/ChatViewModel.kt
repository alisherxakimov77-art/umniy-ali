package com.umniyali.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umniyali.app.data.repository.ChatRepository
import com.umniyali.app.data.repository.SettingsRepository
import com.umniyali.app.domain.model.ChatMessage
import com.umniyali.app.domain.model.ChatResponse
import com.umniyali.app.domain.model.UserSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _settings = MutableStateFlow(UserSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    init {
        viewModelScope.launch {
            chatRepository.messages.collect {
                _messages.value = it
            }
        }
        viewModelScope.launch {
            settingsRepository.settingsFlow.collect {
                _settings.value = it
            }
        }
    }

    fun sendMessage(text: String, onSpoken: ((String) -> Unit)? = null) {
        if (text.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            chatRepository.saveMessage("user", text)
            try {
                val settings = settingsRepository.getSettings()
                val response = chatRepository.sendMessage(text, settings)
                handleResponse(response, onSpoken)
            } catch (e: Exception) {
                val errorText = "Ошибка связи с сервером: ${e.message}"
                chatRepository.saveMessage("assistant", errorText)
                onSpoken?.invoke(errorText)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun handleResponse(response: ChatResponse, onSpoken: ((String) -> Unit)?) {
        when (response.type) {
            "action" -> {
                val action = response.action ?: return
                val text = when (action.action) {
                    "telegram_search" -> {
                        val query = action.params["query"] ?: ""
                        "Ищу $query в Telegram…"
                    }
                    "reminder" -> "Создаю напоминание…"
                    "call" -> "Звоню…"
                    "sms" -> "Отправляю SMS…"
                    "youtube_search" -> "Ищу видео…"
                    "web_search" -> "Ищу в интернете…"
                    "food_calories" -> "Считаю калории по фото…"
                    else -> response.text ?: "Выполняю действие ${action.action}"
                }
                chatRepository.saveMessage("assistant", text)
                onSpoken?.invoke(text)
            }
            else -> {
                val text = response.text ?: "…"
                chatRepository.saveMessage("assistant", text)
                onSpoken?.invoke(text)
            }
        }
    }

    fun setListening(listening: Boolean) {
        _isListening.value = listening
    }

    fun analyzeFood(imageFile: File, onSpoken: ((String) -> Unit)? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            chatRepository.saveMessage("user", "📷 Фото еды")
            try {
                val result = chatRepository.analyzeFood(imageFile)
                val text = buildString {
                    append("${result.dish_name}: примерно ${result.calories} ккал. ")
                    append("Белки ${result.protein_g} г, жиры ${result.fat_g} г, углеводы ${result.carbs_g} г. ")
                    append(result.comment)
                }
                chatRepository.saveMessage("assistant", text)
                onSpoken?.invoke(text)
            } catch (e: Exception) {
                val errorText = "Не удалось проанализировать фото: ${e.message}"
                chatRepository.saveMessage("assistant", errorText)
                onSpoken?.invoke(errorText)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
