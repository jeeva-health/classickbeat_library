package ai.heart.classickbeats.model.response

import com.squareup.moshi.Json

data class GetAgeResponse(
    @Json(name = "age") val id: Int = -1,
)
