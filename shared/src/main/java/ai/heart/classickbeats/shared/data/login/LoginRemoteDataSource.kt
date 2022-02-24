package ai.heart.classickbeats.shared.data.login

import ai.heart.classickbeats.model.request.LoginRequest
import ai.heart.classickbeats.model.request.RefreshTokenRequest
import ai.heart.classickbeats.model.response.LoginResponse
import ai.heart.classickbeats.shared.data.BaseRemoteDataSource
import ai.heart.classickbeats.shared.network.SessionManager
import ai.heart.classickbeats.shared.result.Result
import ai.heart.classickbeats.shared.result.data
import ai.heart.classickbeats.shared.result.error
import ai.heart.classickbeats.shared.result.succeeded
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginRemoteDataSource internal constructor(
    private val loginApiService: LoginApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    sessionManager: SessionManager
) : BaseRemoteDataSource(sessionManager), LoginDataSource {

    override suspend fun login(loginRequest: LoginRequest): Result<LoginResponse.Data> =
        withContext(ioDispatcher) {
            val loginResponse = safeApiCall { loginApiService.login(loginRequest) }
            if (loginResponse.succeeded) {
                return@withContext Result.Success(loginResponse.data!!.responseData!!)
            }
            return@withContext Result.Error(loginResponse.error!!)
        }

    override suspend fun refreshToken(refreshTokenRequest: RefreshTokenRequest): Result<LoginResponse.Data> =
        withContext(ioDispatcher) {
            val refreshTokenResponse =
                safeApiCall { loginApiService.refreshToken(refreshTokenRequest) }
            if (refreshTokenResponse.succeeded) {
                return@withContext Result.Success(refreshTokenResponse.data!!.responseData!!)
            }
            return@withContext Result.Error(refreshTokenResponse.error!!)
        }
}