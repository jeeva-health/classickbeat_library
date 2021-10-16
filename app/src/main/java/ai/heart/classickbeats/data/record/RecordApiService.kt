package ai.heart.classickbeats.data.record

import ai.heart.classickbeats.BuildConfig
import ai.heart.classickbeats.model.entity.*
import ai.heart.classickbeats.model.response.*
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

    @GET("record/logging")
    suspend fun getLoggingData(): Response<LoggingListResponse>

    @GET("record/history/")
    suspend fun getHistoryData(@Query("page") page: Int?): HistoryResponse

    @GET("record/historydetail/record_data.ppg/{id}")
    suspend fun getScanDetail(@Path("id") id: Long): Response<ScanDetailResponse>

    @POST("record/bp/add/")
    suspend fun recordBloodPressure(@Body bpLogEntity: BpLogEntity): Response<ApiResponse>

    @POST("record/glucose/add/")
    suspend fun recordGlucoseLevel(@Body glucoseLogEntity: GlucoseLogEntity): Response<ApiResponse>

    @POST("record/water/add/")
    suspend fun recordWaterIntake(@Body waterLogEntity: WaterLogEntity): Response<ApiResponse>

    @POST("record/weight/add/")
    suspend fun recordWeight(@Body weightLogEntity: WeightLogEntity): Response<ApiResponse>

    @GET("record/sdnn/list")
    suspend fun getSdnnList(): Response<SdnnListResponse>

    @GET("record/timeline/")
    suspend fun getTimelineRecord(
        @Query("type") type: String,
        @Query("page") page: Int?
    ): TimelineResponse

    @GET("record/graph/{model}/")
    suspend fun getGraphData(
        @Path("model") model: String,
        @Query("type") type: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): Response<GraphDataResponse>

    @GET("record/history/")
    suspend fun getHistoryListData(
        @Query("pagination") isPaginated: Boolean,
        @Query("model") model: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): Response<HistoryListResponse>
}