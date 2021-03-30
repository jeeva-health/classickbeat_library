package ai.heart.classickbeats.data.remote

import ai.heart.classickbeats.data.BaseRemoteDataSource
import ai.heart.classickbeats.data.LoginDataSource
import ai.heart.classickbeats.data.model.request.LoginRequest
import ai.heart.classickbeats.data.model.request.RefreshTokenRequest
import ai.heart.classickbeats.data.model.response.LoginResponse
import ai.heart.classickbeats.network.SessionManager
import ai.heart.classickbeats.utils.Result
import ai.heart.classickbeats.utils.data
import ai.heart.classickbeats.utils.error
import ai.heart.classickbeats.utils.succeeded
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginRemoteDataSource internal constructor(
    private val apiService: ApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    sessionManager: SessionManager
) : BaseRemoteDataSource(sessionManager), LoginDataSource {

    override suspend fun login(loginRequest: LoginRequest): Result<LoginResponse.Data> =
        withContext(ioDispatcher) {
            val loginResponse = safeApiCall { apiService.login(loginRequest) }
            if (loginResponse.succeeded) {
                return@withContext Result.Success(loginResponse.data!!.responseData!!)
            }
            return@withContext Result.Error(loginResponse.error!!)
        }

    override suspend fun refreshToken(refreshTokenRequest: RefreshTokenRequest): Result<LoginResponse.Data> =
        withContext(ioDispatcher) {
            val refreshTokenResponse = safeApiCall { apiService.refreshToken(refreshTokenRequest) }
            if (refreshTokenResponse.succeeded) {
                return@withContext Result.Success(refreshTokenResponse.data!!.responseData!!)
            }
            return@withContext Result.Error(refreshTokenResponse.error!!)
        }
}
