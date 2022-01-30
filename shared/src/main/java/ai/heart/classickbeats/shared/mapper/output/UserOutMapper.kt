package ai.heart.classickbeats.shared.mapper.output

import ai.heart.classickbeats.shared.mapper.Mapper
import ai.heart.classickbeats.model.User
import ai.heart.classickbeats.model.entity.UserEntity
import javax.inject.Inject

class UserOutMapper @Inject constructor() : Mapper<User, UserEntity> {
    override fun map(input: User): UserEntity {
        val fullName = input.fullName
        val gender = input.gender.valStr
        val height = input.height
        val isHeightInches = input.heightUnit.enumToValue()
        val weight = input.weight
        val isWeightInKgs = input.weightUnit.enumToValue()
        val dob = input.dob

        return UserEntity(
            fullName = fullName,
            gender = gender,
            height = height,
            heightUnit = isHeightInches,
            weight = weight,
            weightUnit = isWeightInKgs,
            dob = dob
        )
    }
}