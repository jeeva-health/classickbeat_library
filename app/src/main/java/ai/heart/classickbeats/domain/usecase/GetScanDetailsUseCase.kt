package ai.heart.classickbeats.domain.usecase

import ai.heart.classickbeats.data.record.RecordRepository
import ai.heart.classickbeats.data.user.UserRepository
import ai.heart.classickbeats.domain.exception.AgeException
import ai.heart.classickbeats.domain.exception.PpgEntityException
import ai.heart.classickbeats.domain.exception.UserException
import ai.heart.classickbeats.mapper.PpgEntityToScanResult
import ai.heart.classickbeats.model.PPGData
import ai.heart.classickbeats.shared.di.IoDispatcher
import ai.heart.classickbeats.shared.domain.UseCase
import ai.heart.classickbeats.shared.result.data
import ai.heart.classickbeats.shared.result.error
import ai.heart.classickbeats.shared.result.succeeded
import ai.heart.classickbeats.shared.util.computeAge
import ai.heart.classickbeats.shared.util.toDate
import androidx.paging.ExperimentalPagingApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
class GetScanDetailsUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val recordRepository: RecordRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : UseCase<Long, PPGData.ScanResult>(dispatcher) {

    override suspend fun execute(parameters: Long): PPGData.ScanResult {
        val recordResult = recordRepository.getScanDetail(parameters)
        val user = userRepository.getUser() ?: throw UserException("User is null")
        return if (recordResult.succeeded) {
            val ppgEntity = recordResult.data ?: throw PpgEntityException("PpgEntity is null")
            val age = user.dob.toDate()?.computeAge() ?: throw AgeException("Age is null")
            PpgEntityToScanResult.map(age, ppgEntity)
        } else {
            throw PpgEntityException(recordResult.error)
        }
    }
}
