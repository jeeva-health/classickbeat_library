package ai.heart.classickbeats.data.user

import ai.heart.classickbeats.model.entity.UserEntity
import ai.heart.classickbeats.model.response.GetUserResponse
import ai.heart.classickbeats.model.response.RegisterResponse
import ai.heart.classickbeats.shared.result.Result

interface UserDataSource {

    suspend fun registerUser(userEntity: UserEntity): Result<RegisterResponse.Data>

    suspend fun getUser(): Result<GetUserResponse.Data>

    suspend fun registerFirebaseToken(firebaseToken: String): Result<Unit>

    suspend fun submitFeedback(feedback: String): Result<Unit>
}