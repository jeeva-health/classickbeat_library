package ai.heart.classickbeats.ui.wellness

import ai.heart.classickbeats.R
import ai.heart.classickbeats.model.MeditationMedia
import ai.heart.classickbeats.model.WellnessModel
import ai.heart.classickbeats.model.WellnessType
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class WellnessViewModel @Inject constructor() : ViewModel() {

    val sleepMeditation = WellnessModel(
        type = WellnessType.SLEEP,
        title = R.string.sleep_meditation_title,
        message = R.string.sleep_meditation_message,
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

    private val bpMeditation = WellnessModel(
        type = WellnessType.BLOOD_PRESSURE,
        title = R.string.bp_meditation_title,
        message = R.string.bp_meditation_message,
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

    private val angerMeditation = WellnessModel(
        type = WellnessType.ANGER,
        title = R.string.anger_meditation_title,
        message = R.string.anger_meditation_message,
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

    private val stressMeditation = WellnessModel(
        type = WellnessType.STRESS,
        title = R.string.stress_meditation_title,
        message = R.string.stress_meditation_message,
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

    private val immunityMeditation = WellnessModel(
        type = WellnessType.IMMUNITY,
        title = R.string.immunity_meditation_title,
        message = R.string.immunity_meditation_message,
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

    val wellnessCategoryMap =
        mapOf(
            WellnessType.SLEEP to sleepMeditation,
            WellnessType.BLOOD_PRESSURE to bpMeditation,
            WellnessType.ANGER to angerMeditation,
            WellnessType.STRESS to stressMeditation,
            WellnessType.IMMUNITY to immunityMeditation
        )
}