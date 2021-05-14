package ai.heart.classickbeats.mapper.output

import ai.heart.classickbeats.mapper.Mapper
import ai.heart.classickbeats.model.User
import ai.heart.classickbeats.model.entity.UserEntity
import javax.inject.Inject

class UserOutMapper @Inject constructor() : Mapper<User, UserEntity> {
    override fun map(input: User): UserEntity {
        val fullName = input.fullName
        val gender = input.gender
        val height = input.height
        val isHeightInches = input.isHeightInches
        val weight = input.weight
        val isWeightInKgs = input.isWeightKgs
        val dob = input.dob

        return UserEntity(
            fullName = fullName,
            gender = gender,
            height = height,
            isHeightInches = isHeightInches,
            weight = weight,
            isWeightKgs = isWeightInKgs,
            dob = dob
        )
    }
}