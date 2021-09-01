package ai.heart.classickbeats.mapper.input

import ai.heart.classickbeats.mapper.Mapper
import ai.heart.classickbeats.model.HistoryRecord
import ai.heart.classickbeats.model.entity.BaseLogEntity
import javax.inject.Inject

class HistoryListMapper @Inject constructor(private val loggingDataMapper: HistoryRecordMapper) :
    Mapper<List<HistoryRecord>, List<BaseLogEntity>> {
    override fun map(input: List<HistoryRecord>): List<BaseLogEntity> {
        val outputList = mutableListOf<BaseLogEntity>()
        input.forEach {
            it.let { loggingData ->
                outputList.add(loggingDataMapper.map(loggingData))
            }
        }
        return outputList.toList()
    }
}