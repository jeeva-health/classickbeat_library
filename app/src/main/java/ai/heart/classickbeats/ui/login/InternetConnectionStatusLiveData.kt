package ai.heart.classickbeats.ui.login

import ai.heart.classickbeats.network.SessionManager
import android.content.SharedPreferences
import androidx.lifecycle.LiveData

class InternetConnectionStatusLiveData(private val sharedPreferences: SharedPreferences) :
    LiveData<Boolean>() {

    private val internetConnectionStatusListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreference, key ->
            if (key == SessionManager.NETWORK_CONNECTED) {
                value = sharedPreference?.getBoolean(key, false)
            }
        }

    override fun onActive() {
        super.onActive()
        sharedPreferences.registerOnSharedPreferenceChangeListener(internetConnectionStatusListener)
    }

    override fun onInactive() {
        super.onInactive()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(
            internetConnectionStatusListener
        )
    }
}