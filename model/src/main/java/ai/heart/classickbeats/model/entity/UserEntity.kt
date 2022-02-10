package ai.heart.classickbeats.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "user")
data class UserEntity(

    @Transient
    @ColumnInfo(name = "id")
    var userId: Int = -1,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "local_id")
    var id: Int = -1,

    @Json(name = "phone")
    @ColumnInfo(name = "phone")
    var phoneNumber: String? = null,

    @Json(name = "email")
    @ColumnInfo(name = "email")
    var emailAddress: String? = null,

    @Json(name = "name")
    @ColumnInfo(name = "name")
    var fullName: String? = null,

    var gender: String? = null,

    @Json(name = "height_standard")
    @ColumnInfo(name = "height_standard")
    var height: Double? = null,

    @Json(name = "height_unit")
    @ColumnInfo(name = "height_unit")
    val heightUnit: Int?,

    @Json(name = "weight_standard")
    @ColumnInfo(name = "weight_standard")
    var weight: Double? = null,

    @Json(name = "weight_unit")
    @ColumnInfo(name = "weight_unit")
    val weightUnit: Int?,

    var dob: String? = null,

    @Json(name = "is_info_added")
    @ColumnInfo(name = "is_info_added")
    var isRegistered: Boolean? = null,

    @Json(name = "is_baseline_set")
    @ColumnInfo(name = "is_baseline_set")
    var isBaselineSet: Boolean? = null,

    @Transient
    @ColumnInfo(name = "created_at")
    var createdAt: Long? = null,

    @Transient
    @ColumnInfo(name = "modified_at")
    var modifiedAt: Long? = null,

    @Json(name = "google_profile_url")
    @ColumnInfo(name = "google_profile_url")
    var googleProfileUrl: String? = null
)