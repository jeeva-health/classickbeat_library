package ai.heart.classickbeats.network.user.remote

import ai.heart.classickbeats.model.entity.UserEntity
import ai.heart.classickbeats.model.request.FeedbackRequest
import ai.heart.classickbeats.model.request.FirebaseTokenRequest
import ai.heart.classickbeats.model.response.GetUserResponse
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

    @POST("/devices/")
    suspend fun registerFirebaseToken(@Body request: FirebaseTokenRequest): Response<Unit>

    @POST("/user/feedback/")
    suspend fun submitUserFeedback(@Body request: FeedbackRequest): Response<Unit>
}