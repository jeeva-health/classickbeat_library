package ai.heart.classickbeats.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "logging")
data class LogEntityDatabase(
    val diastolic: Int?,
    @ColumnInfo(name = "glucoseValue")
    val glucoseValue: Int?,
    @ColumnInfo(name = "statusTag")
    val statusTag: Int?,
    val stressLevel: Int?,
    val systolic: Int?,
    val timeStamp: String?,
    @ColumnInfo(name = "water")
    val waterQuantity: Float?,
    @ColumnInfo(name = "weightValue")
    val weight: Float?,
    val note: String?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var isUploaded: Boolean = false
}
