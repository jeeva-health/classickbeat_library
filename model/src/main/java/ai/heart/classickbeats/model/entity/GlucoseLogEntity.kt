package ai.heart.classickbeats.model.entity

import ai.heart.classickbeats.model.LogType
import com.squareup.moshi.Json

data class GlucoseLogEntity(
    @Json(name = "glucoseValue")
    val glucoseLevel: Int,
    @Json(name = "statusTag")
    val tag: Int,
    val timeStamp: String? = null,
    val note: String? = null
) : BaseLogEntity(LogType.GlucoseLevel)
