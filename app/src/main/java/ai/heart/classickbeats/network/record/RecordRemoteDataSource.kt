package ai.heart.classickbeats.network.record

import ai.heart.classickbeats.model.entity.*
import ai.heart.classickbeats.model.response.*
import ai.heart.classickbeats.shared.data.BaseRemoteDataSource
import ai.heart.classickbeats.shared.network.SessionManager
import ai.heart.classickbeats.shared.result.Result
import ai.heart.classickbeats.shared.result.data
import ai.heart.classickbeats.shared.result.error
import ai.heart.classickbeats.shared.result.succeeded
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecordRemoteDataSource internal constructor(
    private val apiService: RecordApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    sessionManager: SessionManager
) : BaseRemoteDataSource(sessionManager), RecordDataSource {

    override suspend fun recordPPG(ppgEntity: PPGEntity): Result<Long> {
        val response = safeApiCall { apiService.recordPPG(ppgEntity) }
        if (response.succeeded) {
            val ppgId = response.data?.responseData?.id ?: -1L
            return Result.Success(ppgId)
        }
        return Result.Error(response.error)
    }

    override suspend fun updatePPG(ppgId: Long, ppgEntity: PPGEntity): Result<Boolean> {
        val response = safeApiCall { apiService.updatePPG(ppgId, ppgEntity) }
        if (response.succeeded) {
            return Result.Success(true)
        }
        return Result.Error(response.error)
    }

    override suspend fun getScanDetails(id: Long): Result<ScanDetailResponse.ResponseData> {
        val response = safeApiCall { apiService.getScanDetail(id) }
        if (response.succeeded) {
            val data = response.data!!.responseData
            return Result.Success(data)
        }
        return Result.Error(response.error)
    }

    override suspend fun getLoggingData(): Result<LoggingListResponse.LoggingData> {
        val response = safeApiCall { apiService.getLoggingData() }
        if (response.succeeded) {
            val data = response.data!!.responseData
            return Result.Success(data)
        }
        return Result.Error(response.error)
    }

    override suspend fun recordBloodPressure(pressureLogEntity: PressureLogEntity): Result<Long> =
        withContext(ioDispatcher) {
            val response = safeApiCall { apiService.recordBloodPressure(pressureLogEntity) }
            if (response.succeeded) {
                val id = response.data?.responseData?.id ?: -1L
                return@withContext Result.Success(id)
            }
            return@withContext Result.Error(response.error)
        }


    override suspend fun recordGlucoseLevel(glucoseLogEntity: GlucoseLogEntity): Result<Long> =
        withContext(ioDispatcher) {
            val response = safeApiCall { apiService.recordGlucoseLevel(glucoseLogEntity) }
            if (response.succeeded) {
                val id = response.data?.responseData?.id ?: -1L
                return@withContext Result.Success(id)
            }
            return@withContext Result.Error(response.error)
        }


    override suspend fun recordWaterIntake(waterLogEntity: WaterLogEntity): Result<Long> =
        withContext(ioDispatcher) {
            val response = safeApiCall { apiService.recordWaterIntake(waterLogEntity) }
            if (response.succeeded) {
                val id = response.data?.responseData?.id ?: -1L
                return@withContext Result.Success(id)
            }
            return@withContext Result.Error(response.error)
        }


    override suspend fun recordWeight(weightLogEntity: WeightLogEntity): Result<Long> =
        withContext(ioDispatcher) {
            val response = safeApiCall { apiService.recordWeight(weightLogEntity) }
            if (response.succeeded) {
                val id = response.data?.responseData?.id ?: -1L
                return@withContext Result.Success(id)
            }
            return@withContext Result.Error(response.error)
        }

    override suspend fun getGraphData(
        model: String,
        type: String,
        startDate: String,
        endDate: String
    ): Result<List<GraphDataResponse.ResponseData>> = withContext(ioDispatcher) {
        val response =
            safeApiCall { apiService.getGraphData(model, type, startDate, endDate) }
        if (response.succeeded) {
            Result.Success(response.data!!.responseData!!)
        } else {
            Result.Error(response.error)
        }
    }

    override suspend fun getHistoryListData(
        model: String,
        startDate: String,
        endDate: String
    ): Result<HistoryListResponse.ResponseData> = withContext(ioDispatcher) {
        val response = safeApiCall {
            apiService.getHistoryListData(
                isPaginated = false,
                model = model,
                startDate = startDate,
                endDate = endDate
            )
        }
        if (response.succeeded) {
            Result.Success(response.data!!.responseData)
        } else {
            Result.Error(response.error)
        }
    }
}
