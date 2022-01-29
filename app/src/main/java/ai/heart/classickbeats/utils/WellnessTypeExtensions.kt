package ai.heart.classickbeats.utils

import ai.heart.classickbeats.R
import ai.heart.classickbeats.model.WellnessType

fun WellnessType.getTitle() = when (this) {
    WellnessType.SLEEP -> R.string.sleep_meditation_title
    WellnessType.BLOOD_PRESSURE -> R.string.bp_meditation_title
    WellnessType.ANGER -> R.string.anger_meditation_title
    WellnessType.STRESS -> R.string.stress_meditation_title
    WellnessType.IMMUNITY -> R.string.immunity_meditation_title
}

fun WellnessType.getName() = when (this) {
    WellnessType.SLEEP -> R.string.sleep
    WellnessType.BLOOD_PRESSURE -> R.string.blood_pressure
    WellnessType.ANGER -> R.string.anger
    WellnessType.STRESS -> R.string.stress
    WellnessType.IMMUNITY -> R.string.immunity
}

fun WellnessType.getBackgroundImage() = when (this) {
    WellnessType.SLEEP -> R.drawable.bg_star_2
    WellnessType.BLOOD_PRESSURE -> R.drawable.bg_curved_lines_2
    WellnessType.STRESS -> R.drawable.bg_contour_2
    WellnessType.IMMUNITY -> R.drawable.bg_shade_2
    else -> 0
}

fun WellnessType.getReminderMessage() = when (this) {
    WellnessType.SLEEP -> R.string.reminder_sleep_message
    WellnessType.BLOOD_PRESSURE -> R.string.reminder_bp_message
    WellnessType.ANGER -> R.string.reminder_anger_message
    WellnessType.STRESS -> R.string.reminder_stress_message
    WellnessType.IMMUNITY -> R.string.reminder_immunity_message
}