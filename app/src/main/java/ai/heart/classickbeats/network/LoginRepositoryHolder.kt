package ai.heart.classickbeats.network

import ai.heart.classickbeats.data.login.LoginRepository
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class LoginRepositoryHolder @Inject constructor() {
    var loginRepository: LoginRepository? = null
}