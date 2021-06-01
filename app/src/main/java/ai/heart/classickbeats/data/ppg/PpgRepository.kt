package ai.heart.classickbeats.data.ppg

import ai.heart.classickbeats.model.entity.PPGEntity
import ai.heart.classickbeats.shared.result.Result
import ai.heart.classickbeats.shared.result.error
import dagger.hilt.android.scopes.ActivityRetainedScoped
import timber.log.Timber
import javax.inject.Inject

@ActivityRetainedScoped
class PpgRepository @Inject constructor(
    private val ppgRemoteDataSource: PpgRemoteDataSource,
) {
    suspend fun recordPPG(ppgEntity: PPGEntity): Result<Long> =
        ppgRemoteDataSource.recordPPG(ppgEntity)

    suspend fun updatePPG(ppgId: Long, ppgEntity: PPGEntity): Result<Boolean> =
        ppgRemoteDataSource.updatePPG(ppgId, ppgEntity)

    suspend fun getSdnnList(): Result<List<Double>> {
        val response = ppgRemoteDataSource.getSdnnList()
        when (response) {
            is Result.Success -> {
                val doubleList = response.data.sdnn_list.map { it.toDouble() }
                return Result.Success(doubleList)
            }
            is Result.Error -> Timber.e(response.exception)
            Result.Loading -> throw IllegalStateException("getUser response invalid state")
        }
        return Result.Error(response.error)
    }
}
