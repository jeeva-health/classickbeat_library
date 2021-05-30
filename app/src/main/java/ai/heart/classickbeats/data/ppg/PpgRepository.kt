package ai.heart.classickbeats.data.ppg

import ai.heart.classickbeats.model.entity.PPGEntity
import ai.heart.classickbeats.shared.result.Result
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class PpgRepository @Inject constructor(
    private val ppgRemoteDataSource: PpgRemoteDataSource,
) {
    suspend fun recordPPG(ppgEntity: PPGEntity): Result<Long> =
        ppgRemoteDataSource.recordPPG(ppgEntity)

    suspend fun updatePPG(ppgId: Long, ppgEntity: PPGEntity): Result<Boolean> =
        ppgRemoteDataSource.updatePPG(ppgId, ppgEntity)
}
