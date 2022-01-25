package ai.heart.classickbeats.domain

import ai.heart.classickbeats.R
import ai.heart.classickbeats.model.ScanState
import android.content.Context

fun ScanState.getText(context: Context): String {
    val stringId = when (this) {
        ScanState.Eating -> R.string.chip_eating
        ScanState.Napping -> R.string.chip_napping
        ScanState.Chilling -> R.string.chip_chilling
        ScanState.JustWokeUp -> R.string.chip_just_woke_up
        ScanState.Workout -> R.string.chip_workout
        ScanState.Working -> R.string.chip_working
        ScanState.Others -> R.string.chip_other
    }
    return context.getString(stringId)
}

fun ScanState.getColor(): Int =
    when (this) {
        ScanState.Eating -> R.color.moderate_green_2
        ScanState.Napping -> R.color.bright_red_2
        ScanState.Chilling -> R.color.soft_blue
        ScanState.JustWokeUp -> R.color.very_soft_red_2
        ScanState.Workout -> R.color.dark_red
        ScanState.Working -> R.color.moderate_violet
        ScanState.Others -> R.color.vivid_yellow_2
    }

