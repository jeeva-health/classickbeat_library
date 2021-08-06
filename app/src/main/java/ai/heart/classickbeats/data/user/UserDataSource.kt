package ai.heart.classickbeats.data.user

import ai.heart.classickbeats.model.entity.UserEntity
import ai.heart.classickbeats.model.request.LoginRequest
import ai.heart.classickbeats.model.request.RefreshTokenRequest
import ai.heart.classickbeats.model.response.GetUserResponse
import ai.heart.classickbeats.model.response.LoginResponse
import ai.heart.classickbeats.model.response.RegisterResponse
import ai.heart.classickbeats.shared.result.Result

interface UserDataSource {

    suspend fun registerUser(userEntity: UserEntity): Result<RegisterResponse.Data>

    suspend fun getUser(): Result<GetUserResponse.Data>
}