package ai.heart.classickbeats.shared.data.prefs

import androidx.lifecycle.LiveData

interface PreferenceStorage {
    var onboardingCompleted: Boolean
    var userRegistered: Boolean
    var firstTimeScanCompleted: Boolean
    var scheduleUiHintsShown: Boolean
    var notificationsPreferenceShown: Boolean
    var preferToReceiveNotifications: Boolean
    var snackbarIsStopped: Boolean
    var observableSnackbarIsStopped: LiveData<Boolean>
    var sendUsageStatistics: Boolean
    var userName: String
    var userNumber: String
    var userEmail: String
    var accessToken: String
    var refreshToken: String
    var refreshTokenExpired: Boolean
    var observableRefreshTokenExpired: LiveData<Boolean>
    var networkConnected: Boolean
    var observableNetworkConnected: LiveData<Boolean>

    fun removeAllUserProps()
}

