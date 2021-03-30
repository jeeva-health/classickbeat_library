package ai.heart.classickbeats.data.remote

import ai.heart.classickbeats.BuildConfig
import ai.heart.classickbeats.data.model.request.LoginRequest
import ai.heart.classickbeats.data.model.request.RefreshTokenRequest
import ai.heart.classickbeats.data.model.response.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    companion object {
        const val ENDPOINT = BuildConfig.BASE_URL
    }

    @POST("signup/")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("refresh-token/")
    suspend fun refreshToken(@Body refreshTokenRequest: RefreshTokenRequest): Response<LoginResponse>
}