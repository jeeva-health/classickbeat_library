package ai.heart.classickbeats.data.reminder

import ai.heart.classickbeats.BuildConfig
import ai.heart.classickbeats.model.entity.ReminderEntity
import ai.heart.classickbeats.model.response.ApiResponse
import retrofit2.Response
import retrofit2.http.*

interface ReminderApiService {

    companion object {
        const val ENDPOINT = BuildConfig.BASE_URL
    }

    @POST("reminder/add/")
    suspend fun add(reminderEntity: ReminderEntity): Response<ApiResponse>

    @PATCH("reminder/update/")
    suspend fun update(reminderEntity: ReminderEntity): Response<ApiResponse>

    @GET("reminder/get/{reminderId}")
    suspend fun get(@Path("reminderId") reminderId: Long): Response<ApiResponse>

    @DELETE("reminder/delete/{reminderId}")
    suspend fun delete(@Path("reminderId") reminderId: Long): Response<ApiResponse>

    @GET("reminder/list")
    suspend fun getAll(): Response<ApiResponse>
}