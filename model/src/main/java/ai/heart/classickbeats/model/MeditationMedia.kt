package ai.heart.classickbeats.model

data class MeditationMedia(
    val author: String,
    val duration: Long,
    val isShortType: Boolean = false,
    val resourceUrl: String
)
