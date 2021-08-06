package ai.heart.classickbeats.data.user.remote

import ai.heart.classickbeats.data.user.UserDataSource
import ai.heart.classickbeats.model.entity.UserEntity
import ai.heart.classickbeats.model.response.GetUserResponse
import ai.heart.classickbeats.model.response.RegisterResponse
import ai.heart.classickbeats.shared.data.BaseRemoteDataSource
import ai.heart.classickbeats.shared.network.SessionManager
import ai.heart.classickbeats.shared.result.Result
import ai.heart.classickbeats.shared.result.data
import ai.heart.classickbeats.shared.result.error
import ai.heart.classickbeats.shared.result.succeeded
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRemoteDataSource internal constructor(
    private val userApiService: UserApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    sessionManager: SessionManager
) : BaseRemoteDataSource(sessionManager), UserDataSource {

    override suspend fun registerUser(userEntity: UserEntity): Result<RegisterResponse.Data> =
        withContext(ioDispatcher) {
            val registerResponse = safeApiCall { userApiService.register(userEntity) }
            if (registerResponse.succeeded) {
                return@withContext Result.Success(registerResponse.data!!.responseData)
            }
            return@withContext Result.Error(registerResponse.error!!)
        }

    override suspend fun getUser(): Result<GetUserResponse.Data> {
        val response = safeApiCall { userApiService.fetchUser() }
        if (response.succeeded) {
            return Result.Success(response.data!!.responseData)
        }
        return Result.Error(response.error!!)
    }
}
