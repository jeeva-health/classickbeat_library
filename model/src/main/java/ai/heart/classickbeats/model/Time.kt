package ai.heart.classickbeats.model

data class Time(val hourOfDay: Int, val minute: Int) {

    fun toDisplayString(): String {
        val displayHour = hourOfDay % 12
        val displayMinute = String.format("%02d", minute)
        val hourPeriod = if (hourOfDay < 12) "AM" else "PM"
        return "$displayHour:$displayMinute $hourPeriod"
    }

    fun toSerializeString(): String = "$hourOfDay:$minute"
}

fun String.serializeStringToTime(): Time {
    val (hour, minute) = this.split(":")
    return Time(hourOfDay = hour.toInt(), minute = minute.toInt())
}