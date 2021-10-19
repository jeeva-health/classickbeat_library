package ai.heart.classickbeats.model

sealed class HistoryItem {
    data class LogItem(val timeline: Timeline) : HistoryItem()
    data class DateItem(val date: String) : HistoryItem()
}