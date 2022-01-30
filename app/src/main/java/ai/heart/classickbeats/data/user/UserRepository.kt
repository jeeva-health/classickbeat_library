package ai.heart.classickbeats.data.user

import ai.heart.classickbeats.data.user.cache.UserDao
import ai.heart.classickbeats.data.user.remote.UserRemoteDataSource
import ai.heart.classickbeats.domain.exception.UserException
import ai.heart.classickbeats.model.User
import ai.heart.classickbeats.model.entity.UserEntity
import ai.heart.classickbeats.shared.mapper.input.UserInMapper
import ai.heart.classickbeats.shared.mapper.output.UserOutMapper
import ai.heart.classickbeats.shared.result.Result
import ai.heart.classickbeats.shared.result.data
import ai.heart.classickbeats.shared.result.error
import ai.heart.classickbeats.shared.result.succeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
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

    suspend fun getUserAsFlow(): Flow<User?> {
        refreshUser()
        return userDao.loadFlow()
            .map { value: List<UserEntity?>? -> value?.firstOrNull()?.let { userInMapper.map(it) } }
            .flowOn(Dispatchers.IO)
    }

    suspend fun getUser(): User? {
        val dbEntity = userDao.load().firstOrNull()
        if (dbEntity == null) {
            val result = userRemoteDataSource.getUser()
            if (result.succeeded) {
                return result.data?.user?.let { userInMapper.map(it) }
            } else {
                throw UserException(result.error)
            }
        }
        return dbEntity.let { userInMapper.map(it) }
    }

    suspend fun registerFirebaseToken(firebaseToken: String): Result<Unit> =
        userRemoteDataSource.registerFirebaseToken(firebaseToken)

    suspend fun submitFeedback(feedback: String): Result<Unit> =
        userRemoteDataSource.submitFeedback(feedback)

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
        val FRESH_TIMEOUT = TimeUnit.MINUTES.toMillis(1)
    }
}