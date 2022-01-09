package ai.heart.classickbeats.ui.wellness

import ai.heart.classickbeats.R
import ai.heart.classickbeats.data.meditation.MeditationRepository
import ai.heart.classickbeats.model.MeditationMedia
import ai.heart.classickbeats.model.WellnessModel
import ai.heart.classickbeats.model.WellnessType
import ai.heart.classickbeats.shared.result.data
import ai.heart.classickbeats.shared.result.error
import ai.heart.classickbeats.shared.result.succeeded
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WellnessViewModel @Inject constructor(
    private val meditationRepository: MeditationRepository
) : ViewModel() {

    var meditationList: List<MeditationMedia>? = null

    val meditationFile = MutableLiveData<MeditationMedia>()

    val showLoading = MutableLiveData(false)

    var apiError: String? = null

    fun getMeditationList() {
        viewModelScope.launch {
            showLoading.postValue(true)
            val result = meditationRepository.getMeditationList()
            if (result.succeeded) {
                meditationList = result.data
            } else {
                apiError = result.error
            }
            showLoading.postValue(false)
        }
    }

    fun getMeditationFile(meditationId: Long) {
        viewModelScope.launch {
            showLoading.postValue(true)
            val result = meditationRepository.getMeditationFile(meditationId)
            if (result.succeeded) {
                meditationFile.postValue(result.data!!)
            } else {
                apiError = result.error
            }
            showLoading.postValue(false)
        }
    }

    private val sleepMeditation = WellnessModel(
        type = WellnessType.SLEEP,
        title = R.string.sleep_meditation_title,
        message = R.string.sleep_meditation_message
    )

    private val bpMeditation = WellnessModel(
        type = WellnessType.BLOOD_PRESSURE,
        title = R.string.bp_meditation_title,
        message = R.string.bp_meditation_message
    )

    private val angerMeditation = WellnessModel(
        type = WellnessType.ANGER,
        title = R.string.anger_meditation_title,
        message = R.string.anger_meditation_message
    )

    private val stressMeditation = WellnessModel(
        type = WellnessType.STRESS,
        title = R.string.stress_meditation_title,
        message = R.string.stress_meditation_message
    )

    private val immunityMeditation = WellnessModel(
        type = WellnessType.IMMUNITY,
        title = R.string.immunity_meditation_title,
        message = R.string.immunity_meditation_message
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
