package ai.heart.classickbeats.model

import java.util.Date

data class ScanResult(
    val bpm: Double,
    val aFib: String,
    val quality: String,
    val ageBin: Int,
    val bioAgeResult: Int,
    val activeStar: Int,
    val sdnn: Double,
    val pnn50: Double,
    val rmssd: Double,
    val isActive: Boolean,
    val stress: StressResult,
    val timeStamp: Date
)