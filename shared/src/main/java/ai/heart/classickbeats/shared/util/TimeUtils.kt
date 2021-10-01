package ai.heart.classickbeats.shared.util

import android.annotation.SuppressLint
import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ConstantLocale")
private val locale: Locale = Locale.getDefault()

fun Int.toDurationString(): String {
    val duration = this

    val seconds = duration % 60
    val minutes = (duration / 60) % 60
    val hours = duration / 3600

    val minuteFormatted = String.format("%02d", minutes)
    val secondFormatted = String.format("%02d", seconds)

    return when {
        hours > 0 -> "$hours hr $minuteFormatted min $secondFormatted sec"
        minutes > 0 -> "$minutes min $secondFormatted sec"
        else -> "$seconds sec"
    }
}

fun Date.computeAge(): Int {
    val today = Date()
    return ((today.time - time) / (365 * DateUtils.DAY_IN_MILLIS)).toInt()
}

fun Date.toOrdinalFormattedDateString(): String {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", locale)
    val dateString = dateFormat.format(this)
    val (day, month, year) = dateString.split(" ")
    val formattedDay = Utils.ordinalOf(day.toInt())
    val formattedMonth = month[0] + month.substring(1).lowercase(locale)
    return "$formattedDay $formattedMonth, $year"
}

fun Date.toOrdinalFormattedDateStringWithoutYear(): String {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", locale)
    val dateString = dateFormat.format(this)
    val (day, month, year) = dateString.split(" ")
    val formattedDay = Utils.ordinalOf(day.toInt())
    val formattedMonth = month[0] + month.substring(1).lowercase(locale)
    return "$formattedDay $formattedMonth"
}

fun Date.toDateString(): String {
    val timeFormat = SimpleDateFormat("yyyy-MMM-dd", locale)
    return timeFormat.format(this)
}

fun Date.toDbFormatString(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale)
    return dateFormat.format(this)
}

fun String.toTimeString(): String? {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", locale)
    val date = inputFormat.parse(this)
    val outputFormat = SimpleDateFormat("h:mm a", locale)
    return date?.let { outputFormat.format(date) }
}

fun String.toTimeString2(): String? {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", locale)
    val date = inputFormat.parse(this)
    val outputFormat = SimpleDateFormat("h:mm a", locale)
    return date?.let { outputFormat.format(date) }
}

fun Date.toTimeString(): String {
    val timeFormat = SimpleDateFormat("h:mm a", locale)
    return timeFormat.format(this)
}

fun String.toDate(): Date? {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", locale)
    return dateFormat.parse(this)
}

fun String.toDateStringWithoutTime(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", locale)
    val dateFormat2 = SimpleDateFormat("dd MMM yyyy", locale)
    return dateFormat2.format(let { dateFormat.parse(it)!! })
}

fun Date.toWeekString(): String {
    val startDate = this
    val endDate = Date(this.time + DateUtils.DAY_IN_MILLIS * 6 + DateUtils.HOUR_IN_MILLIS * 12)
    val (_, startMonth, startDay) = startDate.toDateString().split("-")
    val (_, endMonth, endDay) = endDate.toDateString().split("-")
    val formattedStartDay = Utils.ordinalOf(startDay.toInt())
    val formattedStartMonth = startMonth[0] + startMonth.substring(1).lowercase(locale)
    val formattedEndDay = Utils.ordinalOf(endDay.toInt())
    val formattedEndMonth = endMonth[0] + endMonth.substring(1).lowercase(locale)
    return "$formattedStartDay $formattedStartMonth - $formattedEndDay $formattedEndMonth"
}

fun Date.toMonthString(): String {
    val monthFormat = SimpleDateFormat("MMM", locale)
    return monthFormat.format(this)
}

fun Date.getDateSubtractedBy(diff: Int): Date {
    val inputDateTimestamp = this.time
    val requestedDateTimestamp = inputDateTimestamp + DateUtils.DAY_IN_MILLIS * diff
    return Date(requestedDateTimestamp)
}