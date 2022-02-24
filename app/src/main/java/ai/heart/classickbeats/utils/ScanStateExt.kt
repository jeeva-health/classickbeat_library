package ai.heart.classickbeats.utils

import ai.heart.classickbeats.R
import ai.heart.classickbeats.model.ScanState
import android.content.Context

fun ScanState.getText(context: Context): String {
    val stringId = when (this) {
        ScanState.Eating -> R.string.chip_eating
        ScanState.Sleeping -> R.string.chip_sleeping
        ScanState.Chilling -> R.string.chip_chilling
        ScanState.Workout -> R.string.chip_workout
        ScanState.Working -> R.string.chip_working
        ScanState.Others -> R.string.chip_other
    }
    return context.getString(stringId)
}

fun ScanState.getColor(): Int =
    when (this) {
        ScanState.Eating -> R.color.moderate_green_2
        ScanState.Sleeping -> R.color.bright_red_2
        ScanState.Chilling -> R.color.soft_blue
        ScanState.Workout -> R.color.dark_red
        ScanState.Working -> R.color.moderate_violet
        ScanState.Others -> R.color.vivid_yellow_2
    }

