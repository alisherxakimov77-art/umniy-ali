package com.umniyali.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TelegramSearchRequest(
    val query: String,
    val max_price: Double? = null,
    val city: String? = null,
    val condition: String? = null,
    val limit: Int = 5
)

@Serializable
data class ParsedAd(
    val product: String,
    val model: String? = null,
    val price: Double? = null,
    val currency: String? = null,
    val condition: String? = null,
    val city: String? = null,
    val channel: String,
    val message_link: String,
    val posted_at: String? = null
)

@Serializable
data class FoodCaloriesResponse(
    val dish_name: String,
    val calories: Int,
    val protein_g: Float,
    val fat_g: Float,
    val carbs_g: Float,
    val comment: String
)
