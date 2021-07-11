package ai.heart.classickbeats.ui.history

import ai.heart.classickbeats.data.record.RecordRepository
import ai.heart.classickbeats.model.HistoryItem
import ai.heart.classickbeats.model.LogType
import ai.heart.classickbeats.model.User
import ai.heart.classickbeats.model.entity.*
import ai.heart.classickbeats.shared.data.login.LoginRepository
import ai.heart.classickbeats.shared.result.Event
import ai.heart.classickbeats.shared.result.data
import ai.heart.classickbeats.shared.result.error
import ai.heart.classickbeats.shared.result.succeeded
import ai.heart.classickbeats.shared.util.toDateStringWithoutTime
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val recordRepository: RecordRepository
) : ViewModel() {

    var apiError: String? = null

    private var _historyData: List<HistoryItem>? = null
    val historyData: List<HistoryItem>?
        get() = _historyData

    private val _refreshData = MutableLiveData<Event<Unit>>()
    val refreshData: LiveData<Event<Unit>> = _refreshData
    private fun reloadHistoryHomeScreen() {
        _refreshData.postValue(Event(Unit))
    }

    private val _userData = MutableLiveData<Event<User>>()
    val userData: LiveData<Event<User>> = _userData
    private fun setUserDate(user: User) {
        _userData.postValue(Event(user))
    }

    private val _showLoading = MutableLiveData(Event(false))
    val showLoading: LiveData<Event<Boolean>> = _showLoading
    fun setShowLoadingTrue() = _showLoading.postValue(Event(true))
    fun setShowLoadingFalse() = _showLoading.postValue(Event(false))

    fun getHistoryData() {
        viewModelScope.launch {
            setShowLoadingTrue()
            val response = recordRepository.getHistoryData()
            if (response.succeeded) {
                _historyData = response.data?.let { convertLogEntityToHistoryItem(it) }
                reloadHistoryHomeScreen()
            } else {
                apiError = response.error
            }
            setShowLoadingFalse()
        }
    }

    private fun convertLogEntityToHistoryItem(entityList: List<BaseLogEntity>): List<HistoryItem> {
        var currDate = ""
        val outputList = mutableListOf<HistoryItem>()
        Timber.i("entityList size: ${entityList.size}")
        entityList.forEach {
            val date: String?
            when (it.type) {
                LogType.BloodPressure -> {
                    date = (it as BpLogEntity).timeStamp?.toDateStringWithoutTime()
                }
                LogType.GlucoseLevel -> {
                    date = (it as GlucoseLogEntity).timeStamp?.toDateStringWithoutTime()
                }
                LogType.WaterIntake -> {
                    date = (it as WaterLogEntity).timeStamp?.toDateStringWithoutTime()
                }
                LogType.Weight -> {
                    date = (it as WeightLogEntity).timeStamp?.toDateStringWithoutTime()
                }
                LogType.Medicine -> {
                    date = (it as MedicineLogEntity).timeStamp?.toDateStringWithoutTime()
                }
                LogType.PPG -> {
                    date = (it as PPGEntity).timeStamp?.toDateStringWithoutTime()
                }
            }
            Timber.i("type: ${it.type}")
            addDateToList(date ?: "No Date", currDate, outputList)
            currDate = date ?: "No Date"
            outputList.add(HistoryItem.LogItem(it))
        }
        return outputList.toList()
    }

    private fun addDateToList(
        date: String,
        currDate: String,
        outputList: MutableList<HistoryItem>
    ) {
        Timber.i("date: $date and currDate: $currDate")
        if (date != currDate) {
            outputList.add(HistoryItem.DateItem(date))
        }
    }

    fun getUser() {
        viewModelScope.launch {
            setShowLoadingTrue()
            val user = loginRepository.getUser().data ?: throw Exception("User data is null")
            setUserDate(user)
            setShowLoadingFalse()
        }
    }
}