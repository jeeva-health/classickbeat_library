package ai.heart.classickbeats.model.entity

import ai.heart.classickbeats.model.LogType

data class BpLogEntity(
    val systolic: Int,
    val diastolic: Int,
    val timeStamp: String? = null,
    val note: String? = null
) : BaseLogEntity(LogType.BloodPressure)