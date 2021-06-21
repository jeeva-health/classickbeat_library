package ai.heart.classickbeats.model.response

import com.squareup.moshi.Json

data class LoggingListResponse(
    @Json(name = "data")
    val responseData: LoggingData,
    val errorList: List<String>?
) {
    data class LoggingData(
        val logging_list: List<List<Logging>>
    ) {
        data class Logging(
            val fields: Fields,
            val model: String,
        ) {
            data class Fields(
                val diastolic: Int?,
                val glucoseValue: Int?,
                val note: String?,
                val statusTag: Int?,
                val stressLevel: Int?,
                val systolic: Int?,
                val timeStamp: String?,
                val water: String?,
                val weightValue: String?
            )
        }
    }
}