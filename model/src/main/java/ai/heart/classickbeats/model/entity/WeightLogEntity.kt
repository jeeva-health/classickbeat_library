package ai.heart.classickbeats.model.entity

import ai.heart.classickbeats.model.LogType
import com.squareup.moshi.Json

data class WeightLogEntity(
    @Transient
    val id: Int = -1,
    @Json(name = "weightValue")
    val weight: Float,
    val timeStamp: String? = null,
    val note: String? = null
) : BaseLogEntity(LogType.Weight)