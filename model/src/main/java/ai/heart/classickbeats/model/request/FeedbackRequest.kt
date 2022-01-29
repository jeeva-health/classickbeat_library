package ai.heart.classickbeats.model.request

import com.squareup.moshi.Json

data class FeedbackRequest(
    @Json(name = "feedback")
    val feedback: String
)
