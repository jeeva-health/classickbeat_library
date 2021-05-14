package ai.heart.classickbeats.mapper.input

import ai.heart.classickbeats.mapper.Mapper
import ai.heart.classickbeats.model.User
import ai.heart.classickbeats.model.entity.UserEntity
import javax.inject.Inject

class UserInMapper @Inject constructor() : Mapper<UserEntity, User> {
    override fun map(input: UserEntity): User {
        val fullName = input.fullName ?: ""
        val email = input.emailAddress ?: ""
        val phoneNumber = input.phoneNumber
        val gender = input.gender ?: "M"
        val weight = input.weight ?: 70.0
        val isWeightKgs = input.isWeightKgs ?: true
        val height = input.height ?: 70.0
        val isHeightInches = input.isHeightInches ?: true
        val dob = input.dob ?: ""

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