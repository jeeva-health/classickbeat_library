package ai.heart.classickbeats.data.record

import ai.heart.classickbeats.model.entity.*
import ai.heart.classickbeats.shared.result.Result
import ai.heart.classickbeats.shared.result.error
import dagger.hilt.android.scopes.ActivityRetainedScoped
import timber.log.Timber
import javax.inject.Inject

@ActivityRetainedScoped
class RecordRepository @Inject constructor(
    private val ppgRemoteDataSource: RecordRemoteDataSource,
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

    suspend fun recordBloodPressure(bpLogEntity: BpLogEntity): Result<Long> =
        ppgRemoteDataSource.recordBloodPressure(bpLogEntity)

    suspend fun recordGlucoseLevel(glucoseLogEntity: GlucoseLogEntity): Result<Long> =
        ppgRemoteDataSource.recordGlucoseLevel(glucoseLogEntity)

    suspend fun recordWaterIntake(waterLogEntity: WaterLogEntity): Result<Long> =
        ppgRemoteDataSource.recordWaterIntake(waterLogEntity)

    suspend fun recordWeight(weightLogEntity: WeightLogEntity): Result<Long> =
        ppgRemoteDataSource.recordWeight(weightLogEntity)
}
