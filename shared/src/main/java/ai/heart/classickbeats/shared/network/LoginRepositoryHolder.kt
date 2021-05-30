package ai.heart.classickbeats.shared.network

import ai.heart.classickbeats.shared.data.login.LoginRepository
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class LoginRepositoryHolder @Inject constructor() {
    var loginRepository: LoginRepository? = null
}