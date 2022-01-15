package ai.heart.classickbeats.shared.network

import ai.heart.classickbeats.model.AuthToken
import ai.heart.classickbeats.shared.domain.prefs.AuthTokenActionUseCase
import ai.heart.classickbeats.shared.domain.prefs.AuthTokenUseCase
import ai.heart.classickbeats.shared.result.data
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    val authTokenActionUseCase: AuthTokenActionUseCase,
    val authTokenUseCase: AuthTokenUseCase
) {

    suspend fun saveAuthToken(accessToken: String, refreshToken: String) {
        authTokenActionUseCase(AuthToken(accessToken, refreshToken))
    }

    fun fetchAccessToken(): String? {
        var accessToken: String? = null
        //TODO: replace runBlocking with appropriate coroutine
        runBlocking {
            val result = authTokenUseCase(Unit)
            accessToken = result.data?.accessToken
        }
        return accessToken
    }

    fun fetchRefreshToken(): String? {
        var refreshToken: String? = null
        //TODO: replace runBlocking with appropriate coroutine
        runBlocking {
            val result = authTokenUseCase(Unit)
            refreshToken = result.data?.refreshToken
        }
        return refreshToken
    }

    suspend fun removeAuthToken() {
        authTokenActionUseCase(AuthToken())
    }

    // TODO: fix the below code
    fun saveRefreshTokenStatus(isValid: Boolean) {
//        val editor = sharedPreferences.edit()
//        editor.putBoolean(REFRESH_TOKEN_EXPIRED, isValid)
//        editor.apply()
    }

    fun updateNetworkIssueStatus(isConnected: Boolean) {
//        val editor = sharedPreferences.edit()
//        editor.putBoolean(NETWORK_CONNECTED, isConnected)
//        editor.apply()
    }

    fun fetchRefreshTokenStatus(): Boolean {
//        return sharedPreferences.getBoolean(REFRESH_TOKEN_EXPIRED, false)
        return true
    }
}
