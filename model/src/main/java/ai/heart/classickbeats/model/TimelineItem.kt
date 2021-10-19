package ai.heart.classickbeats.model

import ai.heart.classickbeats.model.entity.BaseLogEntity

sealed class TimelineItem {
    data class LogItem(val logEntity: BaseLogEntity) : TimelineItem()
    data class DateItem(val date: String) : TimelineItem()
}