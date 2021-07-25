package ai.heart.classickbeats.mapper.input

import ai.heart.classickbeats.model.Logging
import ai.heart.classickbeats.model.entity.BaseLogEntity
import ai.heart.classickbeats.shared.mapper.Mapper
import javax.inject.Inject

class HistoryListMapper @Inject constructor(private val loggingDataMapper: LoggingDataMapper) :
    Mapper<List<Logging>, List<BaseLogEntity>> {
    override fun map(input: List<Logging>): List<BaseLogEntity> {
        val outputList = mutableListOf<BaseLogEntity>()
        input.forEach {
            it.let { loggingData ->
                outputList.add(loggingDataMapper.map(loggingData))
            }
        }
        return outputList.toList()
    }
}