package ai.heart.classickbeats.domain

import ai.heart.classickbeats.R
import ai.heart.classickbeats.model.entity.GlucoseLogEntity
import android.content.Context
import java.time.ZonedDateTime

data class BloodGlucose(
    val time: ZonedDateTime,
    val reading: Float,
    val tag: TAG,
    val note: String?
) {
    enum class TAG {
        FASTING,
        POST_MEAL,
        BED_TIME,
        RANDOM
    }
}

fun BloodGlucose.TAG.toStringValue(context: Context) = when (this) {
    BloodGlucose.TAG.FASTING -> context.getString(R.string.fasting)
    BloodGlucose.TAG.POST_MEAL -> context.getString(R.string.post_meal)
    BloodGlucose.TAG.BED_TIME -> context.getString(R.string.bedtime)
    BloodGlucose.TAG.RANDOM -> context.getString(R.string.random_time)
}

fun BloodGlucose.TAG.toInt(): Int = when (this) {
    BloodGlucose.TAG.FASTING -> 0
    BloodGlucose.TAG.POST_MEAL -> 1
    BloodGlucose.TAG.BED_TIME -> 2
    BloodGlucose.TAG.RANDOM -> 3
}

fun Int.toBloodGlucoseTAG(): BloodGlucose.TAG = when (this) {
    0 -> BloodGlucose.TAG.FASTING
    1 -> BloodGlucose.TAG.POST_MEAL
    2 -> BloodGlucose.TAG.BED_TIME
    else -> BloodGlucose.TAG.RANDOM
}

fun BloodGlucose.toDto(): GlucoseLogEntity = GlucoseLogEntity(
    glucoseLevel = this.reading.toInt(),
    tag = this.tag.toInt(),
    timeStamp = this.time.toString(),
    note = this.note
)

fun GlucoseLogEntity.toDomain(): BloodGlucose = BloodGlucose(
    time = TODO(),
    reading = this.glucoseLevel.toFloat(),
    tag = this.tag.toBloodGlucoseTAG(),
    note = this.note
)
