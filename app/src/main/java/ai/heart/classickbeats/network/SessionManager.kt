package ai.heart.classickbeats.network

import android.content.SharedPreferences
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class SessionManager @Inject constructor(val sharedPreferences: SharedPreferences) {

    companion object {
        const val ACCESS_TOKEN = "accessToken"
        const val REFRESH_TOKEN = "refreshToken"
        const val REFRESH_TOKEN_EXPIRED = "refreshTokenExpired"
        const val NETWORK_CONNECTED = "networkConnected"
    }

    fun saveAccessToken(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString(ACCESS_TOKEN, token)
        editor.apply()
    }

    fun saveRefreshToken(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString(REFRESH_TOKEN, token)
        editor.apply()
    }

    fun fetchAccessToken(): String? {
        return sharedPreferences.getString(ACCESS_TOKEN, null)
    }

    fun fetchRefreshToken(): String? {
        return sharedPreferences.getString(REFRESH_TOKEN, null)
    }

    fun removeAuthToken() {
        val editor = sharedPreferences.edit()
        editor.remove(ACCESS_TOKEN)
        editor.remove(REFRESH_TOKEN)
        editor.apply()
    }

    fun saveRefreshTokenStatus(isValid: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(REFRESH_TOKEN_EXPIRED, isValid)
        editor.apply()
    }

    fun updateNetworkIssueStatus(isConnected: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(NETWORK_CONNECTED, isConnected)
        editor.apply()
    }

    fun fetchRefreshTokenStatus(): Boolean {
        return sharedPreferences.getBoolean(REFRESH_TOKEN_EXPIRED, false)
    }
}