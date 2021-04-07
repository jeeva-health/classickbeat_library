package ai.heart.classickbeats.data.remote

import ai.heart.classickbeats.BuildConfig
import ai.heart.classickbeats.data.model.entity.PPGEntity
import ai.heart.classickbeats.data.model.request.LoginRequest
import ai.heart.classickbeats.data.model.request.RefreshTokenRequest
import ai.heart.classickbeats.data.model.request.RegisterRequest
import ai.heart.classickbeats.data.model.response.LoginResponse
import ai.heart.classickbeats.data.model.response.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    companion object {
        const val ENDPOINT = BuildConfig.BASE_URL
    }

    @POST("api/v1/signup/")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("api/v1/refresh-token/")
    suspend fun refreshToken(@Body refreshTokenRequest: RefreshTokenRequest): Response<LoginResponse>

    @POST("api/v1/register/")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<RegisterResponse>

    @POST("ppg/add/")
    suspend fun recordPPG(@Body ppgEntity: PPGEntity): Response<PPGEntity>
}