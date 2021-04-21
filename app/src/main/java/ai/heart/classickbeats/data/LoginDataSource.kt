package ai.heart.classickbeats.data

import ai.heart.classickbeats.data.model.entity.PPGEntity
import ai.heart.classickbeats.data.model.request.LoginRequest
import ai.heart.classickbeats.data.model.request.RefreshTokenRequest
import ai.heart.classickbeats.data.model.request.RegisterRequest
import ai.heart.classickbeats.data.model.response.LoginResponse
import ai.heart.classickbeats.data.model.response.RegisterResponse
import ai.heart.classickbeats.utils.Result


interface LoginDataSource {

    suspend fun login(loginRequest: LoginRequest): Result<LoginResponse.Data>

    suspend fun refreshToken(refreshTokenRequest: RefreshTokenRequest): Result<LoginResponse.Data>

    suspend fun registerUser(registerRequest: RegisterRequest): Result<RegisterResponse.Data>

    // temporary will move out to separate class
    suspend fun recordPPG(ppgEntity: PPGEntity): Result<Boolean>
}