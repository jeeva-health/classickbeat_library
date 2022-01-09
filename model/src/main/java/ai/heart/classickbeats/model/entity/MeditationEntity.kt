package ai.heart.classickbeats.model.entity

import com.squareup.moshi.Json

data class MeditationEntity(
    val id: Long,

    val s3Url: String,

    val name: String?,

    val author: Int?,

    val category: Int,

    val course: Int?,

    val description: String?,

    val duration: Int,

    val guided: Boolean,

    @Json(name = "is_short")
    val isShort: Boolean,

    val language: Int?,
)
