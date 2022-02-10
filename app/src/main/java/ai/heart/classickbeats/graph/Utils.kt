package ai.heart.classickbeats.graph

import ai.heart.classickbeats.model.HistoryType
import ai.heart.classickbeats.shared.util.getDayOfWeek
import ai.heart.classickbeats.shared.util.getDayPart
import ai.heart.classickbeats.shared.util.getHourPart
import ai.heart.classickbeats.shared.util.getNumberOfDaysInMonth
import java.util.*

object Utils {

    fun getDuration(type: HistoryType, startDate: Date): Int =
        when (type) {
            HistoryType.Daily -> 1440 // Granularity of one minute
            HistoryType.Weekly -> 7
            HistoryType.Monthly -> startDate.getNumberOfDaysInMonth()
        }

    fun getIndexForDate(type: HistoryType, date: Date): Int =
        when (type) {
            HistoryType.Daily -> {
                date.getHourPart()
            }
            HistoryType.Weekly -> {
                (date.getDayOfWeek() + 5) % 7
            }
            HistoryType.Monthly -> {
                date.getDayPart() - 1
            }
        }
}