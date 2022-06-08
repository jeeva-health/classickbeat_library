package ai.heart.classickbeats.ui.logging.model


data class BloodPressureViewData(
    val timeString: String,
    val dateString: String,
    val systolicLevel: Int,
    val diastolicLevel: Int
)
