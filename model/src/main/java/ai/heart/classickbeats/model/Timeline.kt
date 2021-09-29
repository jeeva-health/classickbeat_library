package ai.heart.classickbeats.model

import java.util.Date

data class Timeline(
    val type: TimelineType,
    val date: Date,
    val model: LogType,
    val diastolicAvg: Int?,
    val hrAvg: Double?,
    val sdnnAvg: Double?,
    val systolicAvg: Int?,
    val avgValue: Double?,
)