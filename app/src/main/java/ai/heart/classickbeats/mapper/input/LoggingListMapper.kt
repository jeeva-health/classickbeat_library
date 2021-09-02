package ai.heart.classickbeats.mapper.input

import ai.heart.classickbeats.mapper.Mapper
import ai.heart.classickbeats.model.entity.BaseLogEntity
import ai.heart.classickbeats.model.response.LoggingListResponse
import javax.inject.Inject

class LoggingListMapper @Inject constructor(
    private val loggingDataMapper: HistoryRecordMapper,
    private val historyRecordNetworkDbMapper: HistoryRecordNetworkDbMapper
) :
    Mapper<LoggingListResponse.LoggingData, List<BaseLogEntity>> {
    override fun map(input: LoggingListResponse.LoggingData): List<BaseLogEntity> {
        val outputList = mutableListOf<BaseLogEntity>()
        input.loggingList.forEach {
            it.firstOrNull()?.let { loggingData ->
                // TODO(Ritesh: Use single mapper instead of two)
                val output = historyRecordNetworkDbMapper.map(loggingData)
                outputList.add(loggingDataMapper.map(output))
            }
        }
        return outputList.toList()
    }
}