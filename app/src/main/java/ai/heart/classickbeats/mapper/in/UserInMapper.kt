package ai.heart.classickbeats.mapper.`in`

import ai.heart.classickbeats.mapper.Mapper
import ai.heart.classickbeats.model.User
import ai.heart.classickbeats.model.entity.UserEntity
import javax.inject.Inject

class UserInMapper @Inject constructor() : Mapper<UserEntity, User> {
    override fun map(input: UserEntity): User {
        val fullName = input.fullName
        val email = input.emailAddress ?: ""
        val phoneNumber = input.phoneNumber
        val gender = input.gender
        val weight = input.weight
        val isWeightKgs = input.isWeightKgs
        val height = input.height
        val isHeightInches = input.isHeightInches
        val dob = input.dob

        return User(
            fullName = fullName,
            gender = gender,
            weight = weight,
            isWeightKgs = isWeightKgs,
            height = height,
            isHeightInches = isHeightInches,
            dob = dob,
            emailAddress = email
        )
    }
}