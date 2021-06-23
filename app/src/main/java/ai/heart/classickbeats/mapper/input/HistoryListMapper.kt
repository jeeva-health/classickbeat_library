package ai.heart.classickbeats.mapper.input

import ai.heart.classickbeats.model.entity.BaseLogEntity
import ai.heart.classickbeats.model.response.HistoryResponse
import ai.heart.classickbeats.shared.mapper.Mapper
import javax.inject.Inject

class HistoryListMapper @Inject constructor(private val loggingDataMapper: LoggingDataMapper) :
    Mapper<HistoryResponse.LoggingData, List<BaseLogEntity>> {
    override fun map(input: HistoryResponse.LoggingData): List<BaseLogEntity> {
        val outputList = mutableListOf<BaseLogEntity>()
        input.loggingList.forEach {
            it.firstOrNull()?.let { loggingData ->
                outputList.add(loggingDataMapper.map(loggingData))
            }
        }
        return outputList.toList()
    }
}