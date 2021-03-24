package ai.heart.classickbeats.data.model.request

import com.squareup.moshi.Json

data class RefreshTokenRequest(
    @Json(name = "client_id") val clientId: String,
    @Json(name = "client_secret") val clientSecret: String,
    @Json(name = "refresh_token") val refreshToken: String
)