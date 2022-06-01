package ai.heart.classickbeats.ui.logging.model

import ai.heart.classickbeats.domain.BloodGlucose

data class BloodGlucoseViewData(
    val timeString: String,
    val dateString: String,
    val reading: Int,
    val tag: BloodGlucose.TAG,
    val note: String?
)
