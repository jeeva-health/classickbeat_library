package ai.heart.classickbeats.model

data class Time(val hourOfDay: Int, val minute: Int) {

    override fun toString(): String {
        val displayHour = hourOfDay % 12
        val hourPeriod = if (hourOfDay < 12) "am" else "pm"
        return "$displayHour:$minute $hourPeriod"
    }
}
