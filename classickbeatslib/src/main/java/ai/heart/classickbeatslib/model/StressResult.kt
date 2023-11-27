package ai.heart.classickbeatslib.model

data class StressResult(
    val stressResult: Int = 0,
    val dataCount: Int,
    val targetDataCount: Int = 6,
    val distinctDataCount: Int,
    val targetDistinctDataCount: Int = 3
)