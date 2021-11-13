package ai.heart.classickbeats.model

data class MeditationMedia(
    val id: Long,
    val name: String,
    val category: String,
    val author: String,
    val duration: Long,
    val isShortType: Boolean,
    val resourceUrl: String,
    val language: Language,
    val isGuided: Boolean
) {
    enum class Language {
        None,
        English,
        Hindi
    }
}
