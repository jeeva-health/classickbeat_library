package ai.heart.classickbeats.shared.data.prefs

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@FlowPreview
@ExperimentalCoroutinesApi
class SharedPreferenceStorage @Inject constructor(
    @ApplicationContext context: Context
) : PreferenceStorage {

    private val prefs: Lazy<SharedPreferences> = lazy { // Lazy to prevent IO access to main thread.
        context.applicationContext.getSharedPreferences(
            PREFS_NAME, Context.MODE_PRIVATE
        ).apply {
            registerOnSharedPreferenceChangeListener(changeListener)
        }
    }

    private val observableShowSnackbarResult = MutableLiveData<Boolean>()

    private val changeListener = OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            PREF_SNACKBAR_IS_STOPPED -> observableShowSnackbarResult.value = snackbarIsStopped
        }
    }

    override var onBoardingCompleted by BooleanPreference(prefs, PREF_ONBOARDING, false)

    override var userRegistered by BooleanPreference(prefs, PREF_USER_REGISTERED, false)

    override var firstTimeScanCompleted by BooleanPreference(
        prefs,
        PREF_FIRST_SCAN_COMPLETED,
        false
    )

    override var scheduleUiHintsShown by BooleanPreference(prefs, PREF_UI_HINTS_SHOWN, false)

    override var notificationsPreferenceShown
            by BooleanPreference(prefs, PREF_NOTIFICATIONS_SHOWN, false)

    override var preferToReceiveNotifications
            by BooleanPreference(prefs, PREF_RECEIVE_NOTIFICATIONS, false)

    override var snackbarIsStopped by BooleanPreference(prefs, PREF_SNACKBAR_IS_STOPPED, false)

    override var observableSnackbarIsStopped: LiveData<Boolean>
        get() {
            observableShowSnackbarResult.value = snackbarIsStopped
            return observableShowSnackbarResult
        }
        set(_) = throw IllegalAccessException("This property can't be changed")

    override var sendUsageStatistics by BooleanPreference(prefs, PREF_SEND_USAGE_STATISTICS, true)

    override var userName: String by StringPreference(prefs, USER_NAME, "User")

    override var userNumber: String by StringPreference(prefs, USER_PHONE_NUMBER, "")

    override var userEmail: String by StringPreference(prefs, USER_EMAIL_ADDRESS, "")

    override var accessToken: String by StringPreference(prefs, ACCESS_TOKEN, "")

    override var refreshToken: String by StringPreference(prefs, REFRESH_TOKEN, "")

    override var refreshTokenExpired: Boolean by BooleanPreference(
        prefs,
        REFRESH_TOKEN_EXPIRED,
        false
    )

    override var observableRefreshTokenExpired: LiveData<Boolean>
        get() = TODO("Not yet implemented")
        set(value) {}

    override var networkConnected: Boolean by BooleanPreference(prefs, NETWORK_CONNECTED, true)

    override var observableNetworkConnected: LiveData<Boolean>
        get() = TODO("Not yet implemented")
        set(value) {}

    companion object {
        const val PREFS_NAME = "my_prefs"
        const val PREF_ONBOARDING = "pref_onboarding"
        const val PREF_USER_REGISTERED = "user_registerd"
        const val PREF_UI_HINTS_SHOWN = "pref_ui_hints_shown"
        const val PREF_NOTIFICATIONS_SHOWN = "pref_notifications_shown"
        const val PREF_RECEIVE_NOTIFICATIONS = "pref_receive_notifications"
        const val PREF_SNACKBAR_IS_STOPPED = "pref_snackbar_is_stopped"
        const val PREF_SEND_USAGE_STATISTICS = "pref_send_usage_statistics"
        const val PREF_DARK_MODE_ENABLED = "pref_dark_mode"
        const val PREF_FIRST_SCAN_COMPLETED = "pref_first_scan_completed"
        const val USER_NAME = "user_name"
        const val USER_PHONE_NUMBER = "user_number"
        const val USER_EMAIL_ADDRESS = "user_email"
        const val ACCESS_TOKEN = "accessToken"
        const val REFRESH_TOKEN = "refreshToken"
        const val REFRESH_TOKEN_EXPIRED = "refreshTokenExpired"
        const val NETWORK_CONNECTED = "networkConnected"
    }

    fun registerOnPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.value.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun removeAllUserProps() {
        val editor = prefs.value.edit()
        editor.remove(USER_NAME)
        editor.remove(USER_PHONE_NUMBER)
        editor.remove(USER_EMAIL_ADDRESS)
        editor.apply()
    }
}

class BooleanPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: Boolean
) : ReadWriteProperty<Any, Boolean> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return preferences.value.getBoolean(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        preferences.value.edit { putBoolean(name, value) }
    }
}

class StringPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: String
) : ReadWriteProperty<Any, String?> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): String {
        return preferences.value.getString(name, defaultValue) ?: defaultValue
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) {
        preferences.value.edit { putString(name, value) }
    }
}