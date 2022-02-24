package ai.heart.classickbeats.domain.usecase

import ai.heart.classickbeats.data.record.RecordRepository
import ai.heart.classickbeats.shared.di.IoDispatcher
import ai.heart.classickbeats.shared.domain.UseCase
import ai.heart.classickbeats.shared.result.Result
import androidx.paging.ExperimentalPagingApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
class DiscardPpgScanResultUseCase @Inject constructor(
    private val recordRepository: RecordRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : UseCase<Long, Boolean>(dispatcher) {

    override suspend fun execute(parameters: Long): Boolean {
        return when (recordRepository.discardPpgData(parameters)) {
            is Result.Success -> true
            is Result.Error -> false
            Result.Loading -> false
        }
    }
}
