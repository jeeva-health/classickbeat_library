package ai.heart.classickbeats.data.record

import ai.heart.classickbeats.mapper.input.HistoryListMapper
import ai.heart.classickbeats.mapper.input.LoggingListMapper
import ai.heart.classickbeats.model.entity.*
import ai.heart.classickbeats.shared.result.Result
import ai.heart.classickbeats.shared.result.error
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

@ActivityRetainedScoped
class RecordRepository @Inject constructor(
    private val recordApiService: RecordApiService,
    private val recordRemoteDataSource: RecordRemoteDataSource,
    private val loggingListMapper: LoggingListMapper,
    private val historyListMapper: HistoryListMapper
) {
    suspend fun recordPPG(ppgEntity: PPGEntity): Result<Long> =
        recordRemoteDataSource.recordPPG(ppgEntity)

    suspend fun updatePPG(ppgId: Long, ppgEntity: PPGEntity): Result<Boolean> =
        recordRemoteDataSource.updatePPG(ppgId, ppgEntity)

    suspend fun getSdnnList(): Result<List<Double>> {
        val response = recordRemoteDataSource.getSdnnList()
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

    suspend fun getLoggingData(): Result<List<BaseLogEntity>> {
        val response = recordRemoteDataSource.getLoggingData()
        when (response) {
            is Result.Success -> {
                val logEntityList = loggingListMapper.map(response.data)
                return Result.Success(logEntityList)
            }
            is Result.Error -> Timber.e(response.exception)
            Result.Loading -> throw IllegalStateException("getLoggingData response invalid state")
        }
        return Result.Error(response.error)
    }

//    suspend fun getHistoryData(): Result<List<BaseLogEntity>> {
//        val response = recordRemoteDataSource.getHistoryData()
//        when (response) {
//            is Result.Success -> {
//                val logEntityList = historyListMapper.map(response.data)
//                return Result.Success(logEntityList)
//            }
//            is Result.Error -> Timber.e(response.exception)
//            Result.Loading -> throw IllegalStateException("getHistoryData response invalid state")
//        }
//        return Result.Error(response.error)
//    }

    fun getHistoryData(): Flow<PagingData<BaseLogEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 200,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                HistoryListPagingSource(
                    recordApiService,
                    historyListMapper
                )
            }
        ).flow
    }

    suspend fun recordBloodPressure(bpLogEntity: BpLogEntity): Result<Long> =
        recordRemoteDataSource.recordBloodPressure(bpLogEntity)

    suspend fun recordGlucoseLevel(glucoseLogEntity: GlucoseLogEntity): Result<Long> =
        recordRemoteDataSource.recordGlucoseLevel(glucoseLogEntity)

    suspend fun recordWaterIntake(waterLogEntity: WaterLogEntity): Result<Long> =
        recordRemoteDataSource.recordWaterIntake(waterLogEntity)

    suspend fun recordWeight(weightLogEntity: WeightLogEntity): Result<Long> =
        recordRemoteDataSource.recordWeight(weightLogEntity)
}