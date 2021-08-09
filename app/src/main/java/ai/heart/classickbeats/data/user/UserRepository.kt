package ai.heart.classickbeats.data.user

import ai.heart.classickbeats.data.user.cache.UserDao
import ai.heart.classickbeats.data.user.remote.UserRemoteDataSource
import ai.heart.classickbeats.mapper.input.UserInMapper
import ai.heart.classickbeats.mapper.output.UserOutMapper
import ai.heart.classickbeats.model.User
import ai.heart.classickbeats.model.entity.UserEntity
import ai.heart.classickbeats.shared.result.Result
import ai.heart.classickbeats.shared.result.error
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@ActivityRetainedScoped
class UserRepository @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userDao: UserDao,
    private val userOutMapper: UserOutMapper,
    private val userInMapper: UserInMapper,
) {

    var loggedInUser: User? = null

    var loginError: String = "Login failed. Please try again"

    suspend fun registerUser(user: User): Result<User> {
        val userEntity = userOutMapper.map(user)
        val response = userRemoteDataSource.registerUser(userEntity)
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

    suspend fun getUser(): Flow<User?> {
        refreshUser()
        return userDao.load()
            .map { value: List<UserEntity?>? -> value?.firstOrNull()?.let { userInMapper.map(it) } }
            .flowOn(Dispatchers.IO)
    }

    private suspend fun refreshUser() {
        // Check if user data was fetched recently.
        val userExists = userDao.hasUser(FRESH_TIMEOUT) > 0
        if (!userExists) {
            // Refreshes the data.
            val user = when (val response = userRemoteDataSource.getUser()) {
                is Result.Success ->
                    response.data.user
                is Result.Error -> {
                    Timber.e(response.exception)
                    null
                }
                Result.Loading -> throw IllegalStateException("getUser response invalid state")
            }

            // Updates the database. Since `userDao.load()` returns an object of
            // `Flow<User>`, a new `User` object is emitted every time there's a
            // change in the `User`  table.
            user?.let { userDao.insertWithTimestamp(it) }
        }
    }

    companion object {
        val FRESH_TIMEOUT = TimeUnit.DAYS.toMillis(1)
    }
}