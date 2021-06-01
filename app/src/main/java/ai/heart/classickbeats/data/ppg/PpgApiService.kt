package ai.heart.classickbeats.data.ppg

import ai.heart.classickbeats.BuildConfig
import ai.heart.classickbeats.model.entity.PPGEntity
import ai.heart.classickbeats.model.response.ApiResponse
import ai.heart.classickbeats.model.response.SdnnListResponse
import retrofit2.Response
import retrofit2.http.*

interface PpgApiService {

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

    @GET("sdnn/list")
    suspend fun getSdnnList(): Response<SdnnListResponse>
}