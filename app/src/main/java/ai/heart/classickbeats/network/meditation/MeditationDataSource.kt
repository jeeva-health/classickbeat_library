package ai.heart.classickbeats.network.meditation

import ai.heart.classickbeats.model.entity.MeditationEntity
import ai.heart.classickbeats.shared.result.Result

interface MeditationDataSource {

    suspend fun getMeditationList(): Result<List<MeditationEntity>>

    suspend fun getMeditationFile(meditationId: Long): Result<MeditationEntity>
}
