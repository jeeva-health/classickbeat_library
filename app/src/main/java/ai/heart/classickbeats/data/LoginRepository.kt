package ai.heart.classickbeats.data

import ai.heart.classickbeats.BuildConfig
import ai.heart.classickbeats.data.model.request.LoginRequest
import ai.heart.classickbeats.data.model.request.RefreshTokenRequest
import ai.heart.classickbeats.data.model.response.LoginResponse
import ai.heart.classickbeats.data.remote.LoginRemoteDataSource
import ai.heart.classickbeats.network.LoginRepositoryHolder
import ai.heart.classickbeats.network.SessionManager
import ai.heart.classickbeats.storage.SharedPreferenceStorage
import ai.heart.classickbeats.utils.Result
import dagger.hilt.android.scopes.ActivityRetainedScoped
import timber.log.Timber
import javax.inject.Inject

@ActivityRetainedScoped
class LoginRepository @Inject constructor(
    private val loginRemoteDataSource: LoginRemoteDataSource,
    private val sessionManager: SessionManager,
    private val sharedPreferenceStorage: SharedPreferenceStorage,
    loginRepositoryHolder: LoginRepositoryHolder
) {

    init {
        loginRepositoryHolder.loginRepository = this
    }

    var loggedInUser: LoginResponse.Data.User? = null

    var loginError: String = "Login failed. Please try again"

    suspend fun loginUser(firebaseToken: String): Boolean {
        val loginRequest = LoginRequest(
            clientId = BuildConfig.CLIENT_ID,
            clientSecret = BuildConfig.CLIENT_SECRET,
            firebaseToken = firebaseToken
        )
        val response = loginRemoteDataSource.login(loginRequest)
        if (response is Result.Success) {
            loggedInUser = response.data.user
            val userName = loggedInUser?.name
            userName?.let {
                sharedPreferenceStorage.userName = it
            }
            val phoneNumber = loggedInUser?.phoneNumber
            phoneNumber?.let {
                sharedPreferenceStorage.userNumber = it
            }
            val accessToken = response.data.accessToken
            val refreshToken = response.data.refreshToken
            if (accessToken != null) {
                sessionManager.saveAccessToken(accessToken)
            }
            if (refreshToken != null) {
                sessionManager.saveRefreshToken(refreshToken)
            }
            return true
        }
        loginError = (response as Result.Error).exception
        return false
    }

    suspend fun refreshToken(): Boolean {
        val refreshToken = sessionManager.fetchRefreshToken() ?: return false
        val refreshTokenRequest = RefreshTokenRequest(
            clientId = BuildConfig.CLIENT_ID,
            clientSecret = BuildConfig.CLIENT_SECRET,
            refreshToken = refreshToken
        )
        when (val response = loginRemoteDataSource.refreshToken(refreshTokenRequest)) {
            is Result.Success -> {
                val updatedAccessToken = response.data.accessToken
                val updatedRefreshToken = response.data.refreshToken
                if (updatedAccessToken != null) {
                    sessionManager.saveAccessToken(updatedAccessToken)
                }
                if (updatedRefreshToken != null) {
                    sessionManager.saveRefreshToken(updatedRefreshToken)
                }
                return true
            }
            is Result.Error -> Timber.e(response.exception)
            Result.Loading -> throw IllegalStateException("refreshToken response invalid state")
        }
        return false
    }
}