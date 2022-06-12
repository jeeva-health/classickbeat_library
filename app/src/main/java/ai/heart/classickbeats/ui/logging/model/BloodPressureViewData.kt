package ai.heart.classickbeats.ui.logging.model

import androidx.compose.runtime.MutableState


data class BloodPressureViewData(
    val timeString: String,
    var dateString: String,
    val systolicLevel: Int,
    val diastolicLevel: Int
)
