package ai.heart.classickbeats.ui.wellness

import ai.heart.classickbeats.model.MeditationMedia
import ai.heart.classickbeats.model.WellnessModel
import ai.heart.classickbeats.model.WellnessType
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class WellnessViewModel @Inject constructor() : ViewModel() {

    val sleepMeditation = WellnessModel(
        type = WellnessType.SLEEP,
        title = "Sleep Well",
        message = "Nothing feels more frustrating than not getting sleep when you are tired and exhausted. Relax your mind and body with \u2028a meditation exercise that puts you into \u2028a place of complete rest.",
        meditationMediaList = listOf(
            MeditationMedia(
                author = "Vipul",
                duration = 540,
                isShortType = true,
                resourceUrl = "s3://public-sound/public.mp3"
            ),
            MeditationMedia(
                author = "Vipul",
                duration = 900,
                isShortType = false,
                resourceUrl = "s3://public-sound/public.mp3"
            )
        )
    )

    val wellnessCategoryMap = mapOf(WellnessType.SLEEP to sleepMeditation)
}