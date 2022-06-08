package ai.heart.classickbeats.network.logging

import ai.heart.classickbeats.domain.BloodGlucose
import ai.heart.classickbeats.domain.BloodPressure
import ai.heart.classickbeats.domain.toDto
import ai.heart.classickbeats.model.entity.BaseLogEntity
import ai.heart.classickbeats.model.entity.BpLogEntity
import ai.heart.classickbeats.model.entity.WaterLogEntity
import ai.heart.classickbeats.model.entity.WeightLogEntity
import ai.heart.classickbeats.shared.mapper.input.LoggingListMapper
import ai.heart.classickbeats.shared.result.Result
import ai.heart.classickbeats.shared.result.error
import androidx.paging.ExperimentalPagingApi
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber
import javax.inject.Inject

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@ActivityRetainedScoped
class LoggingRepository @Inject constructor(
    private val recordRemoteDataSource: LoggingRemoteDataSourceContract,
    private val loggingListMapper: LoggingListMapper,
) {
    suspend fun getLoggingData(): Result<List<BaseLogEntity>> {
        val response = recordRemoteDataSource.getLoggingData()
        when (response) {
            is Result.Success -> {
                val output: MutableList<BaseLogEntity> = mutableListOf()
                response.data.loggingList.forEach {
                    output.add(loggingListMapper.map(it).first())
                }
                return Result.Success(output.toList())
            }
            is Result.Error -> Timber.e(response.exception)
            Result.Loading -> throw IllegalStateException("getLoggingData response invalid state")
        }
        return Result.Error(response.error)
    }

    suspend fun recordBloodPressure(bloodPressure: BloodPressure): Result<Long> =
        recordRemoteDataSource.recordBloodPressure(bloodPressure.toDto())

    suspend fun recordGlucoseLevel(bloodGlucose: BloodGlucose): Result<Long> =
        recordRemoteDataSource.recordGlucoseLevel(bloodGlucose.toDto())

    suspend fun recordWaterIntake(waterLogEntity: WaterLogEntity): Result<Long> =
        recordRemoteDataSource.recordWaterIntake(waterLogEntity)

    suspend fun recordWeight(weightLogEntity: WeightLogEntity): Result<Long> =
        recordRemoteDataSource.recordWeight(weightLogEntity)
}
