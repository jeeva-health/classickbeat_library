package ai.heart.classickbeats.model

data class ScanResult(
    val bpm: Double,
    val hrv: Double,
    val aFib: String,
    val quality: String,
    val ageBin: Int,
    val activeStar: Int,
    val sdnn: Double,
    val pnn50: Double,
    val rmssd: Double,
    val isActive: Boolean,
    val stress: StressResult
)