package ai.heart.classickbeats.model

data class ScanResult(
    val bpm: Double,
    val hrv: Double,
    val aFib: String,
    val quality: String,
    val ageBin: Int,
    val activeStar: Int,
    val isActive: Boolean
)