package ai.heart.classickbeats.data.ppg

import ai.heart.classickbeats.data.BaseRemoteDataSource
import ai.heart.classickbeats.model.entity.PPGEntity
import ai.heart.classickbeats.network.SessionManager
import ai.heart.classickbeats.shared.result.Result
import ai.heart.classickbeats.shared.result.data
import ai.heart.classickbeats.shared.result.error
import ai.heart.classickbeats.shared.result.succeeded
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PpgRemoteDataSource internal constructor(
    private val ppgApiService: PpgApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    sessionManager: SessionManager
) : BaseRemoteDataSource(sessionManager), PpgDataSource {

    override suspend fun recordPPG(ppgEntity: PPGEntity): Result<Long> =
        withContext(ioDispatcher) {
            val response = safeApiCall { ppgApiService.recordPPG(ppgEntity) }
            if (response.succeeded) {
                val ppgId = response.data?.id ?: -1L
                return@withContext Result.Success(ppgId)
            }
            return@withContext Result.Error(response.error)
        }

    override suspend fun updatePPG(ppgId: Long, ppgEntity: PPGEntity): Result<Boolean> =
        withContext(ioDispatcher) {
            val response = safeApiCall { ppgApiService.updatePPG(ppgId, ppgEntity) }
            if (response.succeeded) {
                return@withContext Result.Success(true)
            }
            return@withContext Result.Error(response.error)
        }

}
