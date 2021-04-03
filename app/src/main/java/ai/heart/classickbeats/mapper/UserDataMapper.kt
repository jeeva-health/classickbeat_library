package ai.heart.classickbeats.mapper

import ai.heart.classickbeats.data.model.entity.UserEntity
import ai.heart.classickbeats.domain.model.User
import javax.inject.Inject

class UserDataMapper @Inject constructor() : Mapper<UserEntity, User> {
    override fun map(input: UserEntity): User {
        val fullName = input.name
        val email = input.email
        val phoneNumber = input.phoneNumber

        return User(
            phoneNumber = phoneNumber,
            name = fullName,
            email = email
        )
    }
}