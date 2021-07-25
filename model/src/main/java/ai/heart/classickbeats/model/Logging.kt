package ai.heart.classickbeats.model

import com.squareup.moshi.Json

data class Logging(
    @Json(name = "pk")
    val id: Int,
    val fields: Fields,
    val model: String,
) {
    data class Fields(
        val hr: String?,
        val diastolic: Int?,
        val glucoseValue: Int?,
        val note: String?,
        val statusTag: Int?,
        val stressLevel: Int?,
        val systolic: Int?,
        val timeStamp: String?,
        val water: String?,
        val weightValue: String?
    )
}