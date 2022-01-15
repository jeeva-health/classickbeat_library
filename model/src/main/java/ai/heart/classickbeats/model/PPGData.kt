package ai.heart.classickbeats.model

import java.util.Date

data class PPGData(
    val scanData: ScanRawData,
    val scanResult: ScanResult,
    val timeStamp: String
) {

    data class ScanRawData(
        val rMeans: List<Int>,
        val gMeans: List<Int>,
        val bMeans: List<Int>,
        val cameraTimeStamps: List<Long>
    )

    data class ScanResult(
        val bpm: Float,
        val aFib: String,
        val quality: Float,
        val ageBin: Int,
        val bioAgeResult: Int,
        val activeStar: Int,
        val activeSedentaryProb: List<Float> = emptyList(),
        val binProbsMAP: List<Float> = emptyList(),
        val sedRatioLog: Float = 0.0f,
        val meanNN: Float = 0.0f,
        val sdnn: Float,
        val pnn50: Float,
        val rmssd: Float,
        val ln: Float = 0.0f,
        val isActive: Boolean,
        val stress: StressResult,
        val filteredRMean: List<Double>,
        val timeStamp: Date
    )
}
