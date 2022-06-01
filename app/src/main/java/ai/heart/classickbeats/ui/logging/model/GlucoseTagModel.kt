package ai.heart.classickbeats.ui.logging.model

import ai.heart.classickbeats.domain.BloodGlucose

data class GlucoseTagModel(
    val icon: Int,
    val tag: BloodGlucose.TAG,
)
