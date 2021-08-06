package ai.heart.classickbeats.data.user

import ai.heart.classickbeats.data.user.remote.UserRemoteDataSource
import ai.heart.classickbeats.mapper.input.UserInMapper
import ai.heart.classickbeats.mapper.output.UserOutMapper
import ai.heart.classickbeats.model.User
import ai.heart.classickbeats.shared.result.Result
import ai.heart.classickbeats.shared.result.error
import dagger.hilt.android.scopes.ActivityRetainedScoped
import timber.log.Timber
import javax.inject.Inject

@ActivityRetainedScoped
class UserRepository @Inject constructor(
    private val loginRemoteDataSource: UserRemoteDataSource,
    private val userOutMapper: UserOutMapper,
    private val userInMapper: UserInMapper
) {

    var loggedInUser: User? = null

    var loginError: String = "Login failed. Please try again"

    suspend fun registerUser(user: User): Result<User> {
        val userEntity = userOutMapper.map(user)
        val response = loginRemoteDataSource.registerUser(userEntity)
        when (response) {
            is Result.Success -> {
                val outputUser = userInMapper.map(response.data.user!!)
                loggedInUser = outputUser
                return Result.Success(outputUser)
            }
            is Result.Error -> Timber.e(response.exception)
            Result.Loading -> throw IllegalStateException("registerUser response invalid state")
        }
        return Result.Error(response.error)
    }

    suspend fun getUser(): Result<User> {
        if (loggedInUser != null) {
            return Result.Success(loggedInUser!!)
        }
        val response = loginRemoteDataSource.getUser()
        when (response) {
            is Result.Success -> {
                val outputUser = userInMapper.map(response.data.user!!)
                loggedInUser = outputUser
                return Result.Success(outputUser)
            }
            is Result.Error -> Timber.e(response.exception)
            Result.Loading -> throw IllegalStateException("getUser response invalid state")
        }
        return Result.Error(response.error)
    }
}