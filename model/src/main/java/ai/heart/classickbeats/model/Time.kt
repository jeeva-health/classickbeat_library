package ai.heart.classickbeats.model

data class Time(val hourOfDay: Int, val minute: Int) {

    override fun toString(): String {
        val displayHour = hourOfDay % 12
        val displayMinute = String.format("%02d", minute)
        val hourPeriod = if (hourOfDay < 12) "AM" else "PM"
        return "$displayHour:$displayMinute $hourPeriod"
    }
}
