package ai.heart.classickbeats.graph

import ai.heart.classickbeats.model.TimelineType
import ai.heart.classickbeats.shared.util.getDayOfWeek
import ai.heart.classickbeats.shared.util.getDayPart
import ai.heart.classickbeats.shared.util.getHourPart
import ai.heart.classickbeats.shared.util.getNumberOfDaysInMonth
import java.util.*

object Utils {

    fun getDuration(type: TimelineType, startDate: Date): Int =
        when (type) {
            TimelineType.Daily -> 1
            TimelineType.Weekly -> 7
            TimelineType.Monthly -> startDate.getNumberOfDaysInMonth()
        }

    fun getIndexForDate(type: TimelineType, date: Date): Int =
        when (type) {
            TimelineType.Daily -> {
                date.getHourPart()
            }
            TimelineType.Weekly -> {
                (date.getDayOfWeek() + 5) % 7
            }
            TimelineType.Monthly -> {
                date.getDayPart() - 1
            }
        }
}