package ai.heart.classickbeats.shared.network

import ai.heart.classickbeats.shared.data.login.LoginRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepositoryHolder @Inject constructor() {
    var loginRepository: LoginRepository? = null
}