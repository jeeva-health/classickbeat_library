package ai.heart.classickbeats.data

import ai.heart.classickbeats.model.entity.PPGEntity
import ai.heart.classickbeats.model.entity.UserEntity
import ai.heart.classickbeats.model.request.LoginRequest
import ai.heart.classickbeats.model.request.RefreshTokenRequest
import ai.heart.classickbeats.model.response.LoginResponse
import ai.heart.classickbeats.model.response.RegisterResponse
import ai.heart.classickbeats.shared.result.Result

interface LoginDataSource {

    suspend fun login(loginRequest: LoginRequest): Result<LoginResponse.Data>

    suspend fun refreshToken(refreshTokenRequest: RefreshTokenRequest): Result<LoginResponse.Data>

    suspend fun registerUser(userEntity: UserEntity): Result<RegisterResponse.Data>

    // temporary will move out to separate class
    suspend fun recordPPG(ppgEntity: PPGEntity): Result<Long>

    // temporary will move out to separate class
    suspend fun updatePPG(ppgId: Long, ppgEntity: PPGEntity): Result<Boolean>
}