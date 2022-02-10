package ai.heart.classickbeats.model

data class Reminder(
    val _id: Long = -1L,
    val time: Time,
    val type: Type,
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

    enum class Type {
        PPG,
        Meditation,
        WaterIntake,
        BloodPressure,
        Glucose,
        Weight,
        Medication
    }
}

fun Reminder.Type.toInt(): Int =
    when (this) {
        Reminder.Type.PPG -> 1
        Reminder.Type.Meditation -> 2
        Reminder.Type.WaterIntake -> 3
        Reminder.Type.BloodPressure -> 4
        Reminder.Type.Glucose -> 5
        Reminder.Type.Weight -> 6
        Reminder.Type.Medication -> 7
    }

fun Int.toReminderType(): Reminder.Type =
    when (this) {
        1 -> Reminder.Type.PPG
        2 -> Reminder.Type.Meditation
        3 -> Reminder.Type.WaterIntake
        4 -> Reminder.Type.BloodPressure
        5 -> Reminder.Type.Glucose
        6 -> Reminder.Type.Weight
        7 -> Reminder.Type.Medication
        else -> Reminder.Type.PPG
    }

fun Reminder.DayOfWeek.toInt(): Int =
    when (this) {
        Reminder.DayOfWeek.Monday -> 1
        Reminder.DayOfWeek.Tuesday -> 2
        Reminder.DayOfWeek.Wednesday -> 3
        Reminder.DayOfWeek.Thursday -> 4
        Reminder.DayOfWeek.Friday -> 5
        Reminder.DayOfWeek.Saturday -> 6
        Reminder.DayOfWeek.Sunday -> 7
    }

fun Int.toReminderDayOfWeek(): Reminder.DayOfWeek =
    when (this) {
        1 -> Reminder.DayOfWeek.Monday
        2 -> Reminder.DayOfWeek.Tuesday
        3 -> Reminder.DayOfWeek.Wednesday
        4 -> Reminder.DayOfWeek.Thursday
        5 -> Reminder.DayOfWeek.Friday
        6 -> Reminder.DayOfWeek.Saturday
        else -> Reminder.DayOfWeek.Sunday
    }
