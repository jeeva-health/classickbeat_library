package ai.heart.classickbeats.model.response

import ai.heart.classickbeats.model.entity.UserEntity
import com.squareup.moshi.Json

data class GetUserResponse(@Json(name = "data") val responseData: Data) {
    data class Data(
        val user: UserEntity?,
    )
}