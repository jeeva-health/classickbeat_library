package ai.heart.classickbeats.shared.data.login

import ai.heart.classickbeats.model.entity.UserEntity
import ai.heart.classickbeats.model.request.LoginRequest
import ai.heart.classickbeats.model.request.RefreshTokenRequest
import ai.heart.classickbeats.model.response.GetUserResponse
import ai.heart.classickbeats.model.response.LoginResponse
import ai.heart.classickbeats.model.response.RegisterResponse
import ai.heart.classickbeats.shared.BuildConfig
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

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

    @GET("/user/users")
    suspend fun fetchUser(): Response<GetUserResponse>
}