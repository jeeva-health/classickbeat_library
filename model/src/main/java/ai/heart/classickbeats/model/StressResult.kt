package ai.heart.classickbeats.model

data class StressResult(
    val stressResult: Int = 0,
    val dataCount: Int,
    val targetDataCount: Int = 10
)