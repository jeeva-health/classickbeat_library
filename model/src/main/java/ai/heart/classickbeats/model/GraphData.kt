package ai.heart.classickbeats.model

import java.util.Date

data class GraphData(
    val model: LogType,
    val timelineType: TimelineType,
    val isDecimal: Boolean = true,
    val valueList: List<Double>,
    val valueList2: List<Double>,
    val dateList: List<Date>,
    val startDate: Date,
    val endDate: Date
)