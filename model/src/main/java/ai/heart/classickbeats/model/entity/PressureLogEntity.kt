package ai.heart.classickbeats.model.entity

import ai.heart.classickbeats.model.LogType
import com.squareup.moshi.Json

data class PressureLogEntity(
    @Transient
    val id: Long = -1,
    @Json(name = "SystolicValue")
    val systolicLevel: Int,
    @Json(name = "DiastolicValue")
    val diastolicLevel: Int,
    val timeStamp: String? = null,
) : BaseLogEntity(LogType.GlucoseLevel)
