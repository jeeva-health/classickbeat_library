package ai.heart.classickbeats.model

data class Reminder(
    val _id: Long = -1L,
    val name: String,
    val time: Time,
    val frequency: List<DayOfWeek>,
    val isReminderSet: Boolean,
    val isReminderActive: Boolean
) {
    enum class DayOfWeek {
        Monday,
        Tuesday,
        Wednesday,
        Thursday,
        Friday,
        Saturday,
        Sunday
    }
}

fun List<Reminder.DayOfWeek>.toDisplayString(): String =
    if (this.size == 1) {
        when (this.first()) {
            Reminder.DayOfWeek.Monday -> "Monday"
            Reminder.DayOfWeek.Tuesday -> "Tuesday"
            Reminder.DayOfWeek.Wednesday -> "Wednesday"
            Reminder.DayOfWeek.Thursday -> "Thursday"
            Reminder.DayOfWeek.Friday -> "Friday"
            Reminder.DayOfWeek.Saturday -> "Saturday"
            Reminder.DayOfWeek.Sunday -> "Sunday"
        }
    } else {
        this.joinToString {
            when (it) {
                Reminder.DayOfWeek.Monday -> "M"
                Reminder.DayOfWeek.Tuesday -> "Tu"
                Reminder.DayOfWeek.Wednesday -> "W"
                Reminder.DayOfWeek.Thursday -> "Th"
                Reminder.DayOfWeek.Friday -> "F"
                Reminder.DayOfWeek.Saturday -> "Sa"
                Reminder.DayOfWeek.Sunday -> "Su"
            }
        }
    }