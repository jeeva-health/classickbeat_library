package ai.heart.classickbeats.shared.data.prefs

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

interface PreferenceStorage {
    var onBoardingCompleted: Boolean
    var scheduleUiHintsShown: Boolean
    var notificationsPreferenceShown: Boolean
    var preferToReceiveNotifications: Boolean
    var snackbarIsStopped: Boolean
    var observableSnackbarIsStopped: LiveData<Boolean>
    var sendUsageStatistics: Boolean
    var selectedTheme: String
    var observableSelectedTheme: Flow<String>
    var userName: String
    var userNumber: String
    var userEmail: String

    fun removeAllUserProps()
}

