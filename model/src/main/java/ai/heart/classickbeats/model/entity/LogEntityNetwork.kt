package ai.heart.classickbeats.model.entity

import com.squareup.moshi.Json


data class LogEntityNetwork(
    @Json(name = "pk")
    val id: Long,
    val fields: Fields,
    val model: String,
) {
    data class Fields(
        val diastolic: Int?,
        @Json(name = "glucoseValue")
        val glucoseValue: Int?,
        val glucoseTag: Int?,
        @Json(name = "statusTag")
        val statusTag: Int?,
        val stressLevel: Int?,
        val systolic: Int?,
        val timeStamp: String?,
        @Json(name = "water")
        val waterQuantity: Float?,
        @Json(name = "weightValue")
        val weight: Float?,
        val note: String?
    )
}
