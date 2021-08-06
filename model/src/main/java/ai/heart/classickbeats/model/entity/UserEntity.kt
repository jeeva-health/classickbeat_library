package ai.heart.classickbeats.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "user")
data class UserEntity(
    @Transient
    @PrimaryKey
    val id: Int? = null,

    @Json(name = "phone")
    @ColumnInfo(name = "phone")
    val phoneNumber: String? = null,

    @Json(name = "email")
    @ColumnInfo(name = "email")
    val emailAddress: String? = null,

    @Json(name = "name")
    @ColumnInfo(name = "name")
    val fullName: String?,

    val gender: String?,

    @Json(name = "height_standard")
    @ColumnInfo(name = "height_standard")
    val height: Double?,

    @Json(name = "height_unit")
    @ColumnInfo(name = "height_unit")
    val heightUnit: Int?,

    @Json(name = "weight_standard")
    @ColumnInfo(name = "weight_standard")
    val weight: Double?,

    @Json(name = "weight_unit")
    @ColumnInfo(name = "weight_unit")
    val weightUnit: Int?,

    val dob: String?,

    @Json(name = "is_info_added")
    @ColumnInfo(name = "is_info_added")
    val isRegistered: Boolean? = null,

    @Json(name = "is_baseline_set")
    @ColumnInfo(name = "is_baseline_set")
    val isBaselineSet: Boolean? = null,

    @Transient
    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @Transient
    @ColumnInfo(name = "modified_at")
    val modifiedAt: Long
)