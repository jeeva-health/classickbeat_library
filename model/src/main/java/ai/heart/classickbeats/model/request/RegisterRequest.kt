package ai.heart.classickbeats.model.request

import com.squareup.moshi.Json


data class RegisterRequest(
    @Json(name = "name") val fullName: String,
    @Json(name = "gender") val gender: String,
    @Json(name = "height") val height: Double,
    @Json(name = "height_inches") val isHeightInches: Boolean,
    @Json(name = "weight") val weight: Double,
    @Json(name = "weight_kgs") val isWeightKgs: Boolean,
    @Json(name = "dob") val dob: String
)