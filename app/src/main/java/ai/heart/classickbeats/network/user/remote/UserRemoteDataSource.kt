package ai.heart.classickbeats.network.user.remote

import ai.heart.classickbeats.network.user.UserDataSource
import ai.heart.classickbeats.model.entity.UserEntity
import ai.heart.classickbeats.model.request.FeedbackRequest
import ai.heart.classickbeats.model.request.FirebaseTokenRequest
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
        return if (response.succeeded)
            Result.Success(response.data!!.responseData)
        else
            Result.Error(response.error!!)
    }

    override suspend fun registerFirebaseToken(firebaseToken: String): Result<Unit> {
        val firebaseTokenRequest = FirebaseTokenRequest(registrationId = firebaseToken)
        val response = safeApiCall { userApiService.registerFirebaseToken(firebaseTokenRequest) }
        return if (response.succeeded)
            Result.Success(Unit)
        else
            Result.Error(response.error!!)
    }

    override suspend fun submitFeedback(feedback: String): Result<Unit> {
        val feedbackRequest = FeedbackRequest(feedback = feedback)
        val response = safeApiCall { userApiService.submitUserFeedback(feedbackRequest) }
        return if (response.succeeded) {
            Result.Success(Unit)
        } else {
            Result.Error(response.error)
        }
    }
}
