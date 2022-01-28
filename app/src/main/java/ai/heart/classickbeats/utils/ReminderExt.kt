package ai.heart.classickbeats.utils

import ai.heart.classickbeats.R
import ai.heart.classickbeats.model.Reminder
import android.content.Context

fun List<Reminder.DayOfWeek>.toDisplayString(context: Context): String =
    if (this.size == 1) {
        val stringResId = when (this.first()) {
            Reminder.DayOfWeek.Monday -> R.string.monday
            Reminder.DayOfWeek.Tuesday -> R.string.tuesday
            Reminder.DayOfWeek.Wednesday -> R.string.wednesday
            Reminder.DayOfWeek.Thursday -> R.string.thursday
            Reminder.DayOfWeek.Friday -> R.string.friday
            Reminder.DayOfWeek.Saturday -> R.string.saturday
            Reminder.DayOfWeek.Sunday -> R.string.sunday
        }
        context.getString(stringResId)
    } else if (this.size == 7) {
        context.getString(R.string.daily)
    } else {
        this.joinToString {
            val stringResId = when (it) {
                Reminder.DayOfWeek.Monday -> R.string.monday_short
                Reminder.DayOfWeek.Tuesday -> R.string.tuesday_short
                Reminder.DayOfWeek.Wednesday -> R.string.wednesday_short
                Reminder.DayOfWeek.Thursday -> R.string.thursday_short
                Reminder.DayOfWeek.Friday -> R.string.friday_short
                Reminder.DayOfWeek.Saturday -> R.string.saturday_short
                Reminder.DayOfWeek.Sunday -> R.string.sunday_short
            }
            context.getString(stringResId)
        }
    }

fun Reminder.Type.toName(context: Context): String {
    val stringResId = when (this) {
        Reminder.Type.PPG -> R.string.reminder_ppg
        Reminder.Type.Meditation -> R.string.reminder_meditation
        Reminder.Type.WaterIntake -> R.string.reminder_water_intake
        Reminder.Type.BloodPressure -> R.string.reminder_blood_pressure
        Reminder.Type.Glucose -> R.string.reminder_blood_glucose
        Reminder.Type.Weight -> R.string.reminder_weight
        Reminder.Type.Medication -> R.string.reminder_medicine
    }
    return context.getString(stringResId)
}
