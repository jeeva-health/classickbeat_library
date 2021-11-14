package ai.heart.classickbeats.model.entity

import com.squareup.moshi.Json


data class TimelineEntityNetwork(
    @Json(name = "pk")
    val id: Long,
    val fields: Fields,
    val model: String,
) {
    data class Fields(
        val hr: String?,
        val sdnn: Float?,
        @Json(name = "mean_nn")
        val meanNN: Float?,
        val rmssd: Float?,
        val pnn50: Float?,
        val diastolic: Int?,
        val glucoseValue: Int?,
        val statusTag: Int?,
        val stressLevel: Int?,
        val systolic: Int?,
        val timeStamp: String?,
        val water: String?,
        val weightValue: String?
    )
}
