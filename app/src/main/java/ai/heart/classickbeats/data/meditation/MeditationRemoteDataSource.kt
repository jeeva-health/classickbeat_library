package ai.heart.classickbeats.data.meditation

import ai.heart.classickbeats.model.entity.MeditationEntity
import ai.heart.classickbeats.shared.data.BaseRemoteDataSource
import ai.heart.classickbeats.shared.network.SessionManager
import ai.heart.classickbeats.shared.result.Result
import ai.heart.classickbeats.shared.result.data
import ai.heart.classickbeats.shared.result.error
import ai.heart.classickbeats.shared.result.succeeded
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MeditationRemoteDataSource internal constructor(
    private val apiService: MeditationApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    sessionManager: SessionManager
) : BaseRemoteDataSource(sessionManager), MeditationDataSource {

    override suspend fun getMeditationList(): Result<List<MeditationEntity>> =
        withContext(ioDispatcher) {
            val response = safeApiCall { apiService.getMeditationFileList() }
            if (response.succeeded) {
                val list = response.data!!.responseData.meditationList
                return@withContext Result.Success(list)
            }
            return@withContext Result.Error(response.error)
        }

    override suspend fun getMeditationFile(meditationId: Long): Result<MeditationEntity> =
        withContext(ioDispatcher) {
            val response = safeApiCall { apiService.getMeditationFile(meditationId) }
            if (response.succeeded) {
                val entity = response.data!!.responseData.meditation
                return@withContext Result.Success(entity)
            }
            return@withContext Result.Error(response.error)
        }
}
