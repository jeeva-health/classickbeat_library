package ai.heart.classickbeats.shared.util

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*

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
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val dateString = dateFormat.format(this)
    val (day, month, year) = dateString.split(" ")
    val formattedDay = Utils.ordinalOf(day.toInt())
    val formattedMonth = month[0] + month.substring(1).toLowerCase(Locale.ROOT)
    return "$formattedDay $formattedMonth, $year"
}

fun Date.toTimeString(): String {
    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    return timeFormat.format(this)
}

fun String.toDate(): Date? {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return dateFormat.parse(this)
}

fun String.toDateStringWithoutTime(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val dateFormat2 = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return dateFormat2.format(let { dateFormat.parse(it) })
}