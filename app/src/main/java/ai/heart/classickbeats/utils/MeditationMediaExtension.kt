package ai.heart.classickbeats.utils

import ai.heart.classickbeats.R
import ai.heart.classickbeats.model.MeditationMedia
import ai.heart.classickbeats.model.MeditationMedia.Language
import android.content.Context
import kotlin.math.roundToInt

fun MeditationMedia.getCategoryName(context: Context): String = when (isGuided) {
    true -> {
        val langStr = when (language) {
            Language.English -> "(${context.getString(R.string.english_lang)})"
            Language.Hindi -> "(${context.getString(R.string.hindi_lang)})"
        }
        "${context.getString(R.string.guided_meditation)} $langStr"
    }
    false -> {
        context.getString(R.string.self_guided_meditation)
    }
}

fun MeditationMedia.getDurationString(context: Context) =
    "${(duration * 1.0 / 60).roundToInt()} ${context.getString(R.string.minutes_short)}"
