package ai.heart.classickbeats.model

import ai.heart.classickbeats.model.entity.BaseLogEntity

sealed class HistoryItem {
    data class LogItem(val logEntity: BaseLogEntity) : HistoryItem()
    data class DateItem(val date: String) : HistoryItem()
}