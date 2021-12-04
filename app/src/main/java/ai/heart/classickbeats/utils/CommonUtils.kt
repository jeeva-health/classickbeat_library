package ai.heart.classickbeats.utils

import ai.heart.classickbeats.model.Date
import ai.heart.classickbeats.model.Time
import java.util.*

fun getCurrentDate(): Date {
    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)
    return Date(day, month + 1, year)
}

fun getCurrentTime(): Time {
    val c = Calendar.getInstance()
    val hour = c.get(Calendar.HOUR_OF_DAY)
    val minute = c.get(Calendar.MINUTE)
    return Time(hour, minute)
}