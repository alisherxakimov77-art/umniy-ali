package com.umniyali.app.data.remote

import com.umniyali.app.domain.model.ChatRequest
import com.umniyali.app.domain.model.ChatResponse
import com.umniyali.app.domain.model.FoodCaloriesResponse
import com.umniyali.app.domain.model.ParsedAd
import com.umniyali.app.domain.model.TelegramSearchRequest
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @POST("api/v1/chat/")
    suspend fun sendChat(@Body request: ChatRequest): ChatResponse

    @POST("api/v1/telegram/search")
    suspend fun searchTelegram(@Body request: TelegramSearchRequest): List<ParsedAd>

    @Multipart
    @POST("api/v1/vision/food-calories")
    suspend fun analyzeFood(
        @Part file: MultipartBody.Part
    ): FoodCaloriesResponse

    @GET("health")
    suspend fun healthCheck(): Map<String, String>
}
