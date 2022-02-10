package ai.heart.classickbeats.data.util

import androidx.room.TypeConverter

object IntStringConverter {
    @TypeConverter
    fun fromString(value: String?): List<Int> = value?.split("-")?.map { it.toInt() } ?: emptyList()

    @TypeConverter
    fun fromArrayList(list: List<Int>?): String = list?.joinToString(separator = "-") ?: ""
}
