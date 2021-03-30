package ai.heart.classickbeats.ui.login

import ai.heart.classickbeats.network.SessionManager
import android.content.SharedPreferences
import androidx.lifecycle.LiveData

class RefreshTokenStatusLiveData(private val sharedPreferences: SharedPreferences) :
    LiveData<Boolean>() {

    private val mRefreshTokenStatusListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreference, key ->
            if (key == SessionManager.REFRESH_TOKEN_EXPIRED) {
                value = sharedPreference?.getBoolean(key, false)
            }
        }

    override fun onActive() {
        super.onActive()
        sharedPreferences.registerOnSharedPreferenceChangeListener(mRefreshTokenStatusListener)
    }

    override fun onInactive() {
        super.onInactive()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(mRefreshTokenStatusListener)
    }
}