package ai.heart.classickbeats.model

import java.util.Date

data class Timeline(
    val type: HistoryType,
    val date: Date,
    val model: LogType,
    val diastolicAvg: Int?,
    val hrAvg: Int?,
    val sdnnAvg: Double?,
    val systolicAvg: Int?,
    val avgValue: Double?,
)