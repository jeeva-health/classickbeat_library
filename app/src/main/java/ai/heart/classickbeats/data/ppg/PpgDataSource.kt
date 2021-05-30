package ai.heart.classickbeats.data.ppg

import ai.heart.classickbeats.model.entity.PPGEntity
import ai.heart.classickbeats.shared.result.Result

interface PpgDataSource {

    suspend fun recordPPG(ppgEntity: PPGEntity): Result<Long>

    suspend fun updatePPG(ppgId: Long, ppgEntity: PPGEntity): Result<Boolean>

}