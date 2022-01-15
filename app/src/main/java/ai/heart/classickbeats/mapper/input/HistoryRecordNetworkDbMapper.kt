package ai.heart.classickbeats.mapper.input

import ai.heart.classickbeats.mapper.Mapper
import ai.heart.classickbeats.model.entity.TimelineEntityDatabase
import ai.heart.classickbeats.model.entity.TimelineEntityNetwork
import javax.inject.Inject


class HistoryRecordNetworkDbMapper @Inject constructor() :
    Mapper<TimelineEntityNetwork, TimelineEntityDatabase> {

    override fun map(input: TimelineEntityNetwork): TimelineEntityDatabase {
        val id = input.id
        val model = input.model
        val diastolic = input.fields.diastolic
        val systolic = input.fields.systolic
        val hr = input.fields.hr
        val sdnn = input.fields.sdnn
        val meanNN = input.fields.meanNN
        val pnn50 = input.fields.pnn50
        val rmssd = input.fields.rmssd
        val stressLevel = input.fields.stressLevel
        val glucoseValue = input.fields.glucoseValue
        val glucoseTag = input.fields.statusTag
        val waterQuantity = input.fields.water
        val weight = input.fields.weightValue
        val timestamp = input.fields.timeStamp
        return TimelineEntityDatabase(
            id = id,
            model = model,
            diastolic = diastolic,
            systolic = systolic,
            hr = hr,
            sdnn = sdnn,
            meanNN = meanNN,
            pnn50 = pnn50,
            rmssd = rmssd,
            stressLevel = stressLevel,
            glucoseValue = glucoseValue,
            glucoseTag = glucoseTag,
            water = waterQuantity,
            weightValue = weight,
            timeStamp = timestamp
        )
    }
}
