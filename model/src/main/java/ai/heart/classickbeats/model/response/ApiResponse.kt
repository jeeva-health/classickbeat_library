package ai.heart.classickbeats.model.response

import com.squareup.moshi.Json

data class ApiResponse(
    @Json(name = "id") val id: Long = -1,
)
