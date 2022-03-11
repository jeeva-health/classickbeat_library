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
        val heartAgeClassification: HeartAgeClassification,
        val activeStar: Int,
        val activeSedentaryProb: List<Float> = emptyList(),
        val binProbsMAP: List<Float> = emptyList(),
        val sedRatioLog: Float = 0.0f,
        val lifestyleCategory: LifestyleCategory,
        val meanNN: Float = 0.0f,
        val sdnn: Float,
        val pnn50: Float,
        val rmssd: Float,
        val ln: Float = 0.0f,
        val isActive: Boolean,
        val stress: StressResult,
        val filteredRMean: List<Double>,
        val timeStamp: Date,
        val isBaselineSet: Boolean,
    ) {
        enum class LifestyleCategory {
            Sedentary,
            ModeratelyActive,
            Active,
            VeryActive
        }

        enum class HeartAgeClassification {
            Good,
            Similar,
            Worse
        }
    }
}

fun Int.toLifestyleCategory(): PPGData.ScanResult.LifestyleCategory =
    when (this) {
        1 -> PPGData.ScanResult.LifestyleCategory.Sedentary
        2 -> PPGData.ScanResult.LifestyleCategory.ModeratelyActive
        3 -> PPGData.ScanResult.LifestyleCategory.Active
        4 -> PPGData.ScanResult.LifestyleCategory.VeryActive
        else -> PPGData.ScanResult.LifestyleCategory.ModeratelyActive
    }

fun Int.toHeartAgeClassification(): PPGData.ScanResult.HeartAgeClassification =
    when (this) {
        1 -> PPGData.ScanResult.HeartAgeClassification.Worse
        0 -> PPGData.ScanResult.HeartAgeClassification.Similar
        else -> PPGData.ScanResult.HeartAgeClassification.Good
    }
