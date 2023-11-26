package ai.heart.classickbeats.domain.usecase

//import ai.heart.classickbeats.data.record.RecordRepository
import ai.heart.classickbeats.domain.exception.PpgEntityException
import ai.heart.classickbeats.model.PPGData
import ai.heart.classickbeats.model.entity.PPGEntity
import ai.heart.classickbeats.shared.di.IoDispatcher
import ai.heart.classickbeats.shared.domain.UseCase
import ai.heart.classickbeats.shared.mapper.PpgEntityToScanResult
import ai.heart.classickbeats.shared.result.Result
import ai.heart.classickbeats.shared.result.data
import androidx.paging.ExperimentalPagingApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
class GetScanDetailsUseCase @Inject constructor(
//    private val userRepository: UserRepository,
//    private val recordRepository: RecordRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : UseCase<Long, PPGData.ScanResult>(dispatcher) {

    companion object {
        const val MAX_RETRY_ATTEMPT = 5
        const val RETRY_DELAY = 2000L
    }

    override suspend fun execute(parameters: Long): PPGData.ScanResult {
        var recordResult: Result<PPGEntity>? = null
        var counter = 0
//        do {
//            recordResult = recordRepository.getScanDetail(parameters)
//            if (!recordResult.succeeded) {
//                throw PpgEntityException(recordResult.error)
//            }
//            val isCalculationCompleted = recordResult.data?.isCalculationComplete ?: false
//            if (isCalculationCompleted) {
//                break
//            } else {
//                counter++
//                if (counter >= MAX_RETRY_ATTEMPT) {
//                    break
//                }
//                delay(RETRY_DELAY)
//            }
//        } while (true)
        val ppgEntity = recordResult?.data ?: throw PpgEntityException("PPG entity data is null")
        return PpgEntityToScanResult.map(ppgEntity)
    }
}
