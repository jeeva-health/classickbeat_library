package ai.heart.classickbeats.model


data class WellnessModel(
    val type: WellnessType,
    val title: String,
    val message: String,
    val meditationMediaList: List<MeditationMedia>
)