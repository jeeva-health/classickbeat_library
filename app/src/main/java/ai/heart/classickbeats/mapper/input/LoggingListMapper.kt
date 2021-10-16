package ai.heart.classickbeats.mapper.input

import ai.heart.classickbeats.mapper.Mapper
import ai.heart.classickbeats.model.HistoryRecordNetwork
import ai.heart.classickbeats.model.entity.BaseLogEntity
import javax.inject.Inject

class LoggingListMapper @Inject constructor(
    private val loggingDataMapper: HistoryRecordMapper,
    private val historyRecordNetworkDbMapper: HistoryRecordNetworkDbMapper
) :
    Mapper<List<HistoryRecordNetwork>, List<BaseLogEntity>> {
    override fun map(input: List<HistoryRecordNetwork>): List<BaseLogEntity> {
        val outputList = mutableListOf<BaseLogEntity>()
        input.forEach { loggingData ->
            // TODO(Ritesh: Use single mapper instead of two)
            val output = historyRecordNetworkDbMapper.map(loggingData)
            outputList.add(loggingDataMapper.map(output))
        }
        return outputList.toList()
    }
}