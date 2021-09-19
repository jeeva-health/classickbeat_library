package ai.heart.classickbeats.domain

import ai.heart.classickbeats.data.record.RecordRepository
import ai.heart.classickbeats.model.entity.PPGEntity
import ai.heart.classickbeats.shared.di.IoDispatcher
import ai.heart.classickbeats.shared.domain.UseCase
import ai.heart.classickbeats.shared.result.data
import androidx.paging.ExperimentalPagingApi
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@ExperimentalPagingApi
class GetRecentPpgScanHistoryDataByDurationUseCase @Inject constructor(
    private val repository: RecordRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) :
    UseCase<String, List<PPGEntity>>(dispatcher) {

    override suspend fun execute(parameters: String): List<PPGEntity> {
        return repository.getPpgHistoryDataByDuration(parameters).data ?: emptyList()
    }
}