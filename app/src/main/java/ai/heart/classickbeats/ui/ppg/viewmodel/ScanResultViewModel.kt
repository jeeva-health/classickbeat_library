package ai.heart.classickbeats.ui.ppg.viewmodel

import ai.heart.classickbeats.domain.usecase.GetScanDetailsUseCase
import ai.heart.classickbeats.model.PPGData
import ai.heart.classickbeats.shared.result.Event
import ai.heart.classickbeats.shared.result.data
import ai.heart.classickbeats.shared.result.error
import ai.heart.classickbeats.shared.result.succeeded
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@HiltViewModel
class ScanResultViewModel @Inject constructor(
    private val getScanDetailsUseCase: GetScanDetailsUseCase
) : ViewModel() {

    var apiError: String? = null

    private val _showLoading = MutableLiveData(Event(false))
    val showLoading: LiveData<Event<Boolean>> = _showLoading
    private fun setShowLoadingTrue() = _showLoading.postValue(Event(true))
    private fun setShowLoadingFalse() = _showLoading.postValue(Event(false))

    private val _scanDetails = MutableLiveData<Event<PPGData.ScanResult>>()
    val scanDetails: LiveData<Event<PPGData.ScanResult>> = _scanDetails
    private fun setScanDetails(scanResult: PPGData.ScanResult) {
        _scanDetails.postValue(Event(scanResult))
    }

    fun getScanDetail(scanId: Long) {
        viewModelScope.launch {
            setShowLoadingTrue()
            val result = getScanDetailsUseCase(scanId)
            if (result.succeeded) {
                setScanDetails(result.data!!)
            } else {
                apiError = result.error
            }
            setShowLoadingFalse()
        }
    }
}