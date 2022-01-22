package ai.heart.classickbeats.model

data class StressResult(
    val stressResult: Int = 0,
    val dataCount: Int,
    val targetDataCount: Int = 10,
    val distinctDataCount: Int,
    val targetDistinctDataCount: Int = 7
)