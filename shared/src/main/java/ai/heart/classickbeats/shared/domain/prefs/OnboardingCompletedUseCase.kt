package ai.heart.classickbeats.shared.domain.prefs

import ai.heart.classickbeats.shared.data.prefs.PreferenceStorage
import ai.heart.classickbeats.shared.di.IoDispatcher
import ai.heart.classickbeats.shared.domain.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class OnboardingCompletedUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<Unit, Boolean>(dispatcher) {
    override suspend fun execute(parameters: Unit): Boolean = preferenceStorage.onboardingCompleted
}