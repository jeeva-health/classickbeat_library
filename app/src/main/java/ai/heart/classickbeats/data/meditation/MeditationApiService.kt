package ai.heart.classickbeats.data.meditation

import ai.heart.classickbeats.BuildConfig
import ai.heart.classickbeats.model.response.MeditationListResponse
import ai.heart.classickbeats.model.response.MeditationResponse
import retrofit2.Response
import retrofit2.http.GET

interface MeditationApiService {

    companion object {
        const val ENDPOINT = BuildConfig.BASE_URL
    }

    @GET("wellness/meditation/list-add/")
    suspend fun getMeditationFileList(): Response<MeditationListResponse>

    @GET("wellness/meditation/get-update/{meditation_id}")
    suspend fun getMeditationFile(mediationId: Long): Response<MeditationResponse>
}
