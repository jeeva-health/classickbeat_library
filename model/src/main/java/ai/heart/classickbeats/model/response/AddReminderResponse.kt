package ai.heart.classickbeats.model.response

import ai.heart.classickbeats.model.entity.ReminderEntity
import com.squareup.moshi.Json

data class AddReminderResponse(
    @Json(name = "success")
    val successStatus: Boolean,
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "data")
    val responseData: ReminderEntity?,
    @Json(name = "error_list")
    val errorList: List<String>?
)