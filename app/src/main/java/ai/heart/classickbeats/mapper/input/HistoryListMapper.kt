package ai.heart.classickbeats.mapper.input

import ai.heart.classickbeats.mapper.Mapper
import ai.heart.classickbeats.model.entity.BaseLogEntity
import ai.heart.classickbeats.model.entity.TimelineEntityNetwork
import javax.inject.Inject


class HistoryListMapper @Inject constructor(
    private val loggingDataMapper: HistoryRecordMapper,
    private val historyRecordNetworkDbMapper: HistoryRecordNetworkDbMapper
) :
    Mapper<List<TimelineEntityNetwork>, List<BaseLogEntity>> {
    override fun map(input: List<TimelineEntityNetwork>): List<BaseLogEntity> {
        val outputList = mutableListOf<BaseLogEntity>()
        input.forEach { loggingData ->
            // TODO(Ritesh: Use single mapper instead of two)
            val output = historyRecordNetworkDbMapper.map(loggingData)
            outputList.add(loggingDataMapper.map(output))
        }
        return outputList.toList()
    }
}
