package ai.heart.classickbeats.shared.domain.prefs

import ai.heart.classickbeats.model.AuthToken
import ai.heart.classickbeats.shared.data.prefs.PreferenceStorage
import ai.heart.classickbeats.shared.di.IoDispatcher
import ai.heart.classickbeats.shared.domain.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class AuthTokenUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<Unit, AuthToken>(dispatcher) {
    override suspend fun execute(parameters: Unit): AuthToken {
        val accessToken = preferenceStorage.accessToken
        val refreshToken = preferenceStorage.refreshToken
        return AuthToken(accessToken, refreshToken)
    }
}
