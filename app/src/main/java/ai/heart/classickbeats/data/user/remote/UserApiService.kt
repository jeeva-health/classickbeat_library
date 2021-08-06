package ai.heart.classickbeats.data.user.remote

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

interface UserApiService {

    companion object {
        const val ENDPOINT = BuildConfig.BASE_URL
    }

    @PATCH("/user/users/")
    suspend fun register(@Body userEntity: UserEntity): Response<RegisterResponse>

    @GET("/user/users")
    suspend fun fetchUser(): Response<GetUserResponse>
}