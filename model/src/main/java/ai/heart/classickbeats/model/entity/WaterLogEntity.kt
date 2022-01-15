package ai.heart.classickbeats.model.entity

import ai.heart.classickbeats.model.LogType
import com.squareup.moshi.Json

data class WaterLogEntity(
    @Transient
    val id: Long = -1,
    @Json(name = "water")
    val quantity: Float,
    val timeStamp: String? = null,
    val note: String? = null
) : BaseLogEntity(LogType.WaterIntake)
