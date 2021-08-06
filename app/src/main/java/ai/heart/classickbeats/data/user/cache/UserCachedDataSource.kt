package ai.heart.classickbeats.data.user.cache

import ai.heart.classickbeats.data.user.UserDataSource
import ai.heart.classickbeats.model.entity.UserEntity
import ai.heart.classickbeats.model.response.GetUserResponse
import ai.heart.classickbeats.model.response.RegisterResponse
import ai.heart.classickbeats.shared.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class UserCachedDataSource internal constructor(
    private val userDao: UserDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : UserDataSource {

    override suspend fun registerUser(userEntity: UserEntity): Result<RegisterResponse.Data> {
        TODO("Need not be implemented")
    }

    override suspend fun getUser(): Result<GetUserResponse.Data> {
        userDao.load()
    }
}