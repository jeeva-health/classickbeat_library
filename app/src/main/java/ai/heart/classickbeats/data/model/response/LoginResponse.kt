package ai.heart.classickbeats.data.model.response

import ai.heart.classickbeats.model.entity.UserEntity
import com.squareup.moshi.Json

data class LoginResponse(@Json(name = "data") val responseData: Data?) {
    data class Data(
        val user: UserEntity?,
        @Json(name = "registered") val isRegistered: Boolean?,
        @Json(name = "access_token") val accessToken: String?,
        @Json(name = "refresh_token") val refreshToken: String?,
        @Json(name = "token_type") val tokenType: String?,
        @Json(name = "expires_in") val expiresIn: Long?,
        val error: String?
    )
}