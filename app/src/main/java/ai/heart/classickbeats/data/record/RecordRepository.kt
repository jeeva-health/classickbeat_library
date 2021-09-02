package ai.heart.classickbeats.data.record

import ai.heart.classickbeats.data.db.AppDatabase
import ai.heart.classickbeats.mapper.input.HistoryRecordMapper
import ai.heart.classickbeats.mapper.input.LoggingListMapper
import ai.heart.classickbeats.model.HistoryRecordDatabase
import ai.heart.classickbeats.model.entity.*
import ai.heart.classickbeats.shared.result.Result
import ai.heart.classickbeats.shared.result.error
import androidx.paging.*
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

@ExperimentalPagingApi
@ActivityRetainedScoped
class RecordRepository @Inject constructor(
    private val database: AppDatabase,
    private val recordRemoteDataSource: RecordRemoteDataSource,
    private val loggingListMapper: LoggingListMapper,
    private val historyMapper: HistoryRecordMapper,
    private val historyRemoteMediator: HistoryRemoteMediator,
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

    fun getHistoryData(): Flow<PagingData<BaseLogEntity>> {
        val pagingSourceFactory = { database.historyDao().loadAll() }
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                maxSize = 5 * NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = historyRemoteMediator,
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData ->
            pagingData.map { historyRecord: HistoryRecordDatabase ->
                historyMapper.map(historyRecord)
            }
        }
    }

    suspend fun getPpgHistoryDataByCount(limit: Int): Result<List<PPGEntity>> {
        return try {
            val historyRecordList: List<HistoryRecordDatabase> =
                database.historyDao()
                    .loadHistoryDataByModel(model = "record_data.ppg", limit = limit)
            val baseLogEntities = historyRecordList.map { historyMapper.map(it) }
            val ppgEntities = baseLogEntities.map { it as PPGEntity }
            Result.Success(ppgEntities)
        } catch (e: Exception) {
            Timber.i(e)
            Result.Error(e.localizedMessage)
        }
    }

    suspend fun getScanDetail(scanId: Long): Result<PPGEntity> {
        val response = recordRemoteDataSource.getScanDetails(scanId)
        when (response) {
            is Result.Success -> {
                val scanDetail = response.data.scanDetail
                return Result.Success(scanDetail)
            }
            is Result.Error -> Timber.e(response.exception)
            Result.Loading -> throw IllegalStateException("getScanDetail response invalid state")
        }
        return Result.Error(response.error)
    }

    suspend fun recordBloodPressure(bpLogEntity: BpLogEntity): Result<Long> =
        recordRemoteDataSource.recordBloodPressure(bpLogEntity)

    suspend fun recordGlucoseLevel(glucoseLogEntity: GlucoseLogEntity): Result<Long> =
        recordRemoteDataSource.recordGlucoseLevel(glucoseLogEntity)

    suspend fun recordWaterIntake(waterLogEntity: WaterLogEntity): Result<Long> =
        recordRemoteDataSource.recordWaterIntake(waterLogEntity)

    suspend fun recordWeight(weightLogEntity: WeightLogEntity): Result<Long> =
        recordRemoteDataSource.recordWeight(weightLogEntity)

    companion object {
        const val NETWORK_PAGE_SIZE = 5
    }
}