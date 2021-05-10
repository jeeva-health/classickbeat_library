package ai.heart.classickbeats.shared.domain.prefs

import ai.heart.classickbeats.model.AuthToken
import ai.heart.classickbeats.shared.data.prefs.PreferenceStorage
import ai.heart.classickbeats.shared.di.IoDispatcher
import ai.heart.classickbeats.shared.domain.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class AuthTokenActionUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<AuthToken, Unit>(dispatcher) {

    override suspend fun execute(parameters: AuthToken) {
        preferenceStorage.accessToken = parameters.accessToken
        preferenceStorage.refreshToken = parameters.refreshToken
    }
}
