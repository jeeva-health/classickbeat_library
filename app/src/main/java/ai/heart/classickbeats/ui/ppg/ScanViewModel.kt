package ai.heart.classickbeats.ui.ppg

import ai.heart.classickbeats.domain.prefs.FirstScanCompleteActionUseCase
import ai.heart.classickbeats.domain.prefs.FistScanCompletedUseCase
import ai.heart.classickbeats.shared.result.Event
import ai.heart.classickbeats.shared.result.data
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val firstScanCompleteActionUseCase: FirstScanCompleteActionUseCase,
    private val firstScanCompletedUseCase: FistScanCompletedUseCase
) : ViewModel() {

    private val _navigateToScanFragment = MutableLiveData<Event<Unit>>()
    val navigateToScanFragment: LiveData<Event<Unit>> = _navigateToScanFragment

    val isFirstTimeScanCompleted = liveData {
        val result = firstScanCompletedUseCase(Unit)
        Timber.i("isFirstTimeScanCompleted: ${result.data}")
        if (result.data == false) {
            emit(Event(false))
        } else {
            emit(Event(true))
        }
    }

    fun setFirstScanCompleted() {
        viewModelScope.launch {
            firstScanCompleteActionUseCase(true)
            Timber.i("firstScanCompletedSet")
        }
    }

    fun getStartedClick() {
        viewModelScope.launch {
            firstScanCompleteActionUseCase(true)
            _navigateToScanFragment.postValue(Event(Unit))
        }
    }
}