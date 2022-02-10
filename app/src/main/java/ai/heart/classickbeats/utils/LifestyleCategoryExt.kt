package ai.heart.classickbeats.utils

import ai.heart.classickbeats.R
import ai.heart.classickbeats.model.PPGData
import android.content.Context

fun PPGData.ScanResult.LifestyleCategory.toLifestyleText(context: Context): String =
    context.getString(
        when (this) {
            PPGData.ScanResult.LifestyleCategory.Sedentary -> R.string.sedentary
            PPGData.ScanResult.LifestyleCategory.ModeratelyActive -> R.string.moderately_active
            PPGData.ScanResult.LifestyleCategory.Active -> R.string.active
            PPGData.ScanResult.LifestyleCategory.VeryActive -> R.string.very_active
        }
    )
