package com.umniyali.app.data.repository

import com.umniyali.app.data.local.MessageDao
import com.umniyali.app.data.local.entity.MessageEntity
import com.umniyali.app.data.remote.ApiService
import com.umniyali.app.domain.model.ChatMessage
import com.umniyali.app.domain.model.ChatRequest
import com.umniyali.app.domain.model.ChatResponse
import com.umniyali.app.domain.model.FoodCaloriesResponse
import com.umniyali.app.domain.model.UserSettings
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ChatRepository(
    private val apiService: ApiService,
    private val messageDao: MessageDao
) {
    val messages: Flow<List<ChatMessage>> = messageDao.getAllMessages().map { list ->
        list.map { ChatMessage(it.role, it.content) }
    }

    suspend fun sendMessage(text: String, settings: UserSettings): ChatResponse {
        val history = messages.first().takeLast(20)
        val request = ChatRequest(
            message = text,
            settings = settings,
            history = history
        )
        return apiService.sendChat(request)
    }

    suspend fun saveMessage(role: String, content: String) {
        messageDao.insert(MessageEntity(role = role, content = content))
    }

    suspend fun analyzeFood(imageFile: File): FoodCaloriesResponse {
        val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)
        return apiService.analyzeFood(body)
    }
}
