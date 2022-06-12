package ai.heart.classickbeats.data.logging

import ai.heart.classickbeats.BuildConfig
import ai.heart.classickbeats.model.entity.PressureLogEntity
import ai.heart.classickbeats.model.entity.GlucoseLogEntity
import ai.heart.classickbeats.model.entity.WaterLogEntity
import ai.heart.classickbeats.model.entity.WeightLogEntity
import ai.heart.classickbeats.model.response.ApiResponse
import ai.heart.classickbeats.model.response.LoggingListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface LoggingApiService {

    companion object {
        const val ENDPOINT = BuildConfig.BASE_URL
    }

    @GET("record/logging")
    suspend fun getLoggingData(): Response<LoggingListResponse>

    @POST("record/bp/add/")
    suspend fun recordBloodPressure(@Body pressureLogEntity: PressureLogEntity): Response<ApiResponse>

    @POST("record/glucose/add/")
    suspend fun recordGlucoseLevel(@Body glucoseLogEntity: GlucoseLogEntity): Response<ApiResponse>

    @POST("record/water/add/")
    suspend fun recordWaterIntake(@Body waterLogEntity: WaterLogEntity): Response<ApiResponse>

    @POST("record/weight/add/")
    suspend fun recordWeight(@Body weightLogEntity: WeightLogEntity): Response<ApiResponse>
}
