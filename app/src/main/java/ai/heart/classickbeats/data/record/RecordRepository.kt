package ai.heart.classickbeats.data.record

import ai.heart.classickbeats.data.db.AppDatabase
import ai.heart.classickbeats.mapper.input.*
import ai.heart.classickbeats.model.*
import ai.heart.classickbeats.model.entity.*
import ai.heart.classickbeats.shared.result.Result
import ai.heart.classickbeats.shared.result.error
import ai.heart.classickbeats.shared.util.toDateStringNetwork
import androidx.paging.*
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@ActivityRetainedScoped
class RecordRepository @Inject constructor(
    private val service: RecordApiService,
    private val database: AppDatabase,
    private val recordRemoteDataSource: RecordRemoteDataSource,
    private val loggingListMapper: LoggingListMapper,
    private val historyMapper: HistoryRecordMapper,
    private val timelineMapper: TimelineMapper,
    private val graphDataMapper: GraphDataMapper,
    private val historyRecordNetworkDbMapper: HistoryRecordNetworkDbMapper
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
                response.data.loggingList.firstOrNull()?.let {
                    val logEntityList = loggingListMapper.map(it)
                    return Result.Success(logEntityList)
                }
            }
            is Result.Error -> Timber.e(response.exception)
            Result.Loading -> throw IllegalStateException("getLoggingData response invalid state")
        }
        return Result.Error(response.error)
    }

    fun getHistoryData(): Flow<PagingData<BaseLogEntity>> {
        val pagingSourceFactory = { database.timelineDao().loadAll() }
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                maxSize = 5 * NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = TimelineRemoteMediator(
                service,
                database,
                historyRecordNetworkDbMapper
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData ->
            pagingData.map { historyRecord: TimelineEntityDatabase ->
                historyMapper.map(historyRecord)
            }
        }
    }

    suspend fun getPpgHistoryDataByCount(limit: Int): Result<List<PPGEntity>> {
        return try {
            val historyRecordList: List<TimelineEntityDatabase> =
                database.timelineDao()
                    .loadTimelineDataByModelAndCount(model = "record_data.ppg", limit = limit)
            val baseLogEntities = historyRecordList.map { historyMapper.map(it) }
            val ppgEntities = baseLogEntities.map { it as PPGEntity }
            Result.Success(ppgEntities)
        } catch (e: Exception) {
            Timber.i(e)
            Result.Error(e.localizedMessage)
        }
    }

    suspend fun getPpgHistoryDataByDuration(startDate: String): Result<List<PPGEntity>> {
        return try {
            val historyRecordList: List<TimelineEntityDatabase> =
                database.timelineDao()
                    .loadTimelineDataByModelAndDuration(
                        model = "record_data.ppg",
                        startTimeStamp = startDate
                    )
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

    suspend fun getGraphData(
        model: LogType,
        type: HistoryType,
        startDate: Date,
        endDate: Date
    ): Result<GraphData> {
        val modelStr = model.getStringValue()
        val typeStr = type.value
        val startDateStr = startDate.toDateStringNetwork()
        val endDateStr = endDate.toDateStringNetwork()
        val response =
            recordRemoteDataSource.getGraphData(modelStr, typeStr, startDateStr, endDateStr)
        when (response) {
            is Result.Success -> {
                val mapperInput = GraphDataMapper.InputData(
                    model = model,
                    type = type,
                    startDate = startDate,
                    endDate = endDate,
                    response = response.data
                )
                return Result.Success(graphDataMapper.map(mapperInput))
            }
            is Result.Error -> Timber.e(response.exception)
            Result.Loading -> throw IllegalStateException("getGraphData response invalid state")
        }
        return Result.Error(response.error)
    }

    fun getHistoryData(type: HistoryType): Flow<PagingData<Timeline>> {
        val pagingSourceFactory = { database.historyDao().loadByType(type.value) }
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                maxSize = 5 * NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = HistoryRemoteMediator(
                type,
                service,
                database
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow.mapLatest { pagingData ->
            pagingData.map { timelineEntity: HistoryEntity ->
                timelineMapper.map(timelineEntity)
            }
        }
    }

    suspend fun getHistoryListData(
        model: LogType,
        startDate: Date,
        endDate: Date
    ): Result<List<BaseLogEntity>> {
        val modelStr = model.getStringValue()
        val startDateStr = startDate.toDateStringNetwork()
        val endDateStr = endDate.toDateStringNetwork()
        val response = recordRemoteDataSource.getHistoryListData(modelStr, startDateStr, endDateStr)
        when (response) {
            is Result.Success -> {
                val logEntityList = loggingListMapper.map(response.data.historyList)
                return Result.Success(logEntityList)
            }
            is Result.Error -> Timber.e(response.exception)
            Result.Loading -> throw IllegalStateException("getHistoryListData response invalid state")
        }
        return Result.Error(response.error)
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 5
    }
}