package ai.heart.classickbeatslib.shared.util

import android.annotation.SuppressLint
import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ConstantLocale")
private val locale: Locale = Locale.getDefault()



fun Date.toDbFormatString(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale)
    return dateFormat.format(this)
}


fun Date.getDateAddedBy(diff: Int): Date {
    val inputDateTimestamp = this.time
    val requestedDateTimestamp = inputDateTimestamp + DateUtils.DAY_IN_MILLIS * diff
    return Date(requestedDateTimestamp)
}

fun String.toPPGDate(): Date? {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", locale)
    dateFormat.timeZone = TimeZone.getTimeZone("GMT")
    return dateFormat.parse(this)
}

