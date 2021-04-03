package ai.heart.classickbeats.data.model.request

import com.squareup.moshi.Json


data class RegisterRequest(
    @Json(name = "name") val fullName: String
)