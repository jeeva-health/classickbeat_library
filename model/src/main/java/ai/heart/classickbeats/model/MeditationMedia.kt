package ai.heart.classickbeats.model

data class MeditationMedia(
    val id: Long,
    val name: String,
    val wellnessType: WellnessType,
    val author: String = "",
    val duration: Int,
    val resourceUrl: String,
    val language: Language,
    val isGuided: Boolean
) {
    enum class Language {
        English,
        Hindi
    }
}

fun Int.getMeditationLanguage(): MeditationMedia.Language =
    when (this) {
        1 -> MeditationMedia.Language.Hindi
        else -> MeditationMedia.Language.English
    }

fun MeditationMedia.Language.getInt(): Int =
    when (this) {
        MeditationMedia.Language.English -> 0
        MeditationMedia.Language.Hindi -> 1
    }
