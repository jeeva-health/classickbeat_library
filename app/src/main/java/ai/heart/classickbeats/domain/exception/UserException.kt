package ai.heart.classickbeats.domain.exception

import ai.heart.classickbeats.shared.domain.exception.CustomException

class UserException(reason: String?) : CustomException(reason)