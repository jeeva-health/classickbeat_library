package ai.heart.classickbeats.data

import ai.heart.classickbeats.data.model.request.LoginRequest
import ai.heart.classickbeats.data.model.request.RefreshTokenRequest
import ai.heart.classickbeats.data.model.response.LoginResponse
import ai.heart.classickbeats.utils.Result


interface LoginDataSource {

    suspend fun login(loginRequest: LoginRequest): Result<LoginResponse.Data>

    suspend fun refreshToken(refreshTokenRequest: RefreshTokenRequest): Result<LoginResponse.Data>
}