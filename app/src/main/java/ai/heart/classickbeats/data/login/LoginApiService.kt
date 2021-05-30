package ai.heart.classickbeats.data.login

import ai.heart.classickbeats.BuildConfig
import ai.heart.classickbeats.model.entity.UserEntity
import ai.heart.classickbeats.model.request.LoginRequest
import ai.heart.classickbeats.model.request.RefreshTokenRequest
import ai.heart.classickbeats.model.response.LoginResponse
import ai.heart.classickbeats.model.response.RegisterResponse
import retrofit2.Response
import retrofit2.http.*

interface LoginApiService {

    companion object {
        const val ENDPOINT = BuildConfig.BASE_URL
    }

    @POST("api/v1/signup/")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("api/v1/refresh-token/")
    suspend fun refreshToken(@Body refreshTokenRequest: RefreshTokenRequest): Response<LoginResponse>

    @PATCH("/user/users/")
    suspend fun register(@Body userEntity: UserEntity): Response<RegisterResponse>
}