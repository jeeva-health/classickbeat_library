package ai.heart.classickbeats.network.reminder

import ai.heart.classickbeats.BuildConfig
import ai.heart.classickbeats.model.entity.ReminderEntity
import ai.heart.classickbeats.model.response.AddReminderResponse
import ai.heart.classickbeats.model.response.ReminderListResponse
import retrofit2.Response
import retrofit2.http.*

interface ReminderApiService {

    companion object {
        const val ENDPOINT = BuildConfig.BASE_URL
    }

    @POST("reminder/add/")
    suspend fun add(@Body reminderEntity: ReminderEntity): Response<AddReminderResponse>

    @PATCH("reminder/get/{reminderId}")
    suspend fun update(
        @Path("reminderId") reminderId: Long,
        @Body reminderEntity: ReminderEntity
    ): Response<AddReminderResponse>

    @GET("reminder/get/{reminderId}")
    suspend fun get(@Path("reminderId") reminderId: Long): Response<AddReminderResponse>

    @DELETE("reminder/get/{reminderId}")
    suspend fun delete(@Path("reminderId") reminderId: Long): Response<AddReminderResponse>

    @GET("reminder/list")
    suspend fun getAll(): Response<ReminderListResponse>
}
