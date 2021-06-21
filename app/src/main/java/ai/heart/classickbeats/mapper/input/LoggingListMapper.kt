package ai.heart.classickbeats.mapper.input

import ai.heart.classickbeats.model.entity.BaseLogEntity
import ai.heart.classickbeats.model.response.LoggingListResponse
import ai.heart.classickbeats.shared.mapper.Mapper
import javax.inject.Inject

class LoggingListMapper @Inject constructor(private val loggingDataMapper: LoggingDataMapper) :
    Mapper<LoggingListResponse.LoggingData, List<BaseLogEntity>> {
    override fun map(input: LoggingListResponse.LoggingData): List<BaseLogEntity> {
        val outputList = mutableListOf<BaseLogEntity>()
        input.logging_list.forEach {
            it.firstOrNull()?.let { loggingData ->
                outputList.add(loggingDataMapper.map(loggingData))
            }
        }
        return outputList.toList()
    }
}