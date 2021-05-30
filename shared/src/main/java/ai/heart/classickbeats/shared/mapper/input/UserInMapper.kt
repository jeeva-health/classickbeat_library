package ai.heart.classickbeats.shared.mapper.input

import ai.heart.classickbeats.shared.mapper.Mapper
import ai.heart.classickbeats.model.Gender
import ai.heart.classickbeats.model.User
import ai.heart.classickbeats.model.entity.UserEntity
import javax.inject.Inject

class UserInMapper @Inject constructor() : Mapper<UserEntity, User> {
    override fun map(input: UserEntity): User {
        val fullName = input.fullName ?: ""
        val email = input.emailAddress ?: ""
        val phoneNumber = input.phoneNumber
        val gender = input.gender?.let { Gender.valueOf(it) } ?: Gender.MALE
        val weight = input.weight ?: 70.0
        val isWeightKgs = input.isWeightKgs ?: true
        val height = input.height ?: 70.0
        val isHeightInches = input.isHeightInches ?: true
        val dob = input.dob ?: ""
        val isRegistered = input.isRegistered ?: false

        return User(
            fullName = fullName,
            gender = gender,
            weight = weight,
            isWeightKgs = isWeightKgs,
            height = height,
            isHeightInches = isHeightInches,
            dob = dob,
            emailAddress = email,
            isRegistered = isRegistered
        )
    }
}