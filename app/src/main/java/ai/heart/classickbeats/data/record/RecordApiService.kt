package ai.heart.classickbeats.data.record

import ai.heart.classickbeats.BuildConfig
import ai.heart.classickbeats.model.entity.*
import ai.heart.classickbeats.model.response.ApiResponse
import ai.heart.classickbeats.model.response.SdnnListResponse
import retrofit2.Response
import retrofit2.http.*

interface RecordApiService {

    companion object {
        const val ENDPOINT = BuildConfig.BASE_URL
    }

    @POST("record/ppg/add/")
    suspend fun recordPPG(@Body ppgEntity: PPGEntity): Response<ApiResponse>

    @PATCH("record/ppg/update/{ppgId}")
    suspend fun updatePPG(
        @Path("ppgId") ppgId: Long,
        @Body ppgEntity: PPGEntity
    ): Response<ApiResponse>

    @POST("record/bp/add/")
    suspend fun recordBloodPressure(@Body bpLogEntity: BpLogEntity): Response<ApiResponse>

    @POST("record/glucose/add/")
    suspend fun recordGlucoseLevel(@Body glucoseLogEntity: GlucoseLogEntity): Response<ApiResponse>

    @POST("record/water/add/")
    suspend fun recordWaterIntake(@Body waterLogEntity: WaterLogEntity): Response<ApiResponse>

    @POST("record/weight/add/")
    suspend fun recordWeight(@Body weightLogEntity: WeightLogEntity): Response<ApiResponse>

    @GET("sdnn/list")
    suspend fun getSdnnList(): Response<SdnnListResponse>
}