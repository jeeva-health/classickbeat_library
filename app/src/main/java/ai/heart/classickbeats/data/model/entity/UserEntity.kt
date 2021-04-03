package ai.heart.classickbeats.data.model.entity

import com.squareup.moshi.Json

data class UserEntity(
    @Json(name = "phone") val phoneNumber: String,
    val name: String?,
    val email: String?,
    val id: Int
)