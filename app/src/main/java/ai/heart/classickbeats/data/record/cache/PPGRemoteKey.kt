package ai.heart.classickbeats.data.record.cache

import androidx.room.PrimaryKey

data class PPGRemoteKey(
    @PrimaryKey
    val ppgId: Long,
    val prevKey: Int?,
    val nextKey: Int?
)