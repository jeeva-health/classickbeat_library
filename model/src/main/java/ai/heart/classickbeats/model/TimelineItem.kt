package ai.heart.classickbeats.model

sealed class TimelineItem {
    data class LogItem(val timeline: Timeline) : TimelineItem()
    data class DateItem(val date: String) : TimelineItem()
}