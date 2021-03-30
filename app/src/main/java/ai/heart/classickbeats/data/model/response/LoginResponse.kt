package ai.heart.classickbeats.data.model.response

import com.squareup.moshi.Json

data class LoginResponse(@Json(name = "data") val responseData: Data?) {
    data class Data(
        val user: User?,
        @Json(name = "access_token") val accessToken: String?,
        @Json(name = "refresh_token") val refreshToken: String?,
        @Json(name = "token_type") val tokenType: String?,
        @Json(name = "expires_in") val expiresIn: Long?,
        val error: String?
    ) {
        data class User(
            @Json(name = "phone") val phoneNumber: String,
            val name: String?,
            val email: String?,
            val id: Int
        )
    }
}