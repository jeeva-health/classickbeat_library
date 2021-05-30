package ai.heart.classickbeats.data.login

import ai.heart.classickbeats.BuildConfig
import ai.heart.classickbeats.mapper.input.UserInMapper
import ai.heart.classickbeats.mapper.output.UserOutMapper
import ai.heart.classickbeats.model.User
import ai.heart.classickbeats.model.request.LoginRequest
import ai.heart.classickbeats.model.request.RefreshTokenRequest
import ai.heart.classickbeats.network.LoginRepositoryHolder
import ai.heart.classickbeats.network.SessionManager
import ai.heart.classickbeats.shared.data.prefs.PreferenceStorage
import ai.heart.classickbeats.shared.result.Result
import ai.heart.classickbeats.shared.result.error
import dagger.hilt.android.scopes.ActivityRetainedScoped
import timber.log.Timber
import javax.inject.Inject

@ActivityRetainedScoped
class LoginRepository @Inject constructor(
    private val loginRemoteDataSource: LoginRemoteDataSource,
    private val sessionManager: SessionManager,
    private val preferenceStorage: PreferenceStorage,
    private val userOutMapper: UserOutMapper,
    private val userInMapper: UserInMapper,
    loginRepositoryHolder: LoginRepositoryHolder
) {

    init {
        loginRepositoryHolder.loginRepository = this
    }

    var loggedInUser: User? = null

    var loginError: String = "Login failed. Please try again"

    suspend fun loginUser(firebaseToken: String): Pair<Boolean, Boolean> {
        val loginRequest = LoginRequest(
            clientId = BuildConfig.CLIENT_ID,
            clientSecret = BuildConfig.CLIENT_SECRET,
            firebaseToken = firebaseToken
        )
        val response = loginRemoteDataSource.login(loginRequest)
        if (response is Result.Success) {
            loggedInUser = userInMapper.map(response.data.user!!)
            val isUserRegistered = loggedInUser?.isRegistered ?: false
            val accessToken = response.data.accessToken ?: ""
            val refreshToken = response.data.refreshToken ?: ""
            sessionManager.saveAuthToken(accessToken, refreshToken)
            return Pair(true, isUserRegistered)
        }
        loginError = (response as Result.Error).exception!!
        return Pair(false, second = false)
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
                val updatedAccessToken = response.data.accessToken ?: ""
                val updatedRefreshToken = response.data.refreshToken ?: ""
                sessionManager.saveAuthToken(updatedAccessToken, updatedRefreshToken)
                return true
            }
            is Result.Error -> Timber.e(response.exception)
            Result.Loading -> throw IllegalStateException("refreshToken response invalid state")
        }
        return false
    }

    suspend fun registerUser(user: User): Result<User> {
        val userEntity = userOutMapper.map(user)
        val response = loginRemoteDataSource.registerUser(userEntity)
        when (response) {
            is Result.Success -> {
                val outputUser = userInMapper.map(response.data.user!!)
                loggedInUser = outputUser
                return Result.Success(outputUser)
            }
            is Result.Error -> Timber.e(response.exception)
            Result.Loading -> throw IllegalStateException("registerUser response invalid state")
        }
        return Result.Error(response.error)
    }
}