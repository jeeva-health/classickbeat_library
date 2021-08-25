package ai.heart.classickbeats.model.request

import com.squareup.moshi.Json

data class FirebaseTokenRequest(
    @Json(name = "registration_id")
    val registrationId: String,
    val type: String = "android"
)