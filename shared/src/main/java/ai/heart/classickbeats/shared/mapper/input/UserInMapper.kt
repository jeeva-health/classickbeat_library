package ai.heart.classickbeats.shared.mapper.input

import ai.heart.classickbeats.model.*
import ai.heart.classickbeats.model.entity.UserEntity
import ai.heart.classickbeats.shared.mapper.Mapper
import javax.inject.Inject

class UserInMapper @Inject constructor() : Mapper<UserEntity, User> {
    override fun map(input: UserEntity): User {
        val fullName = input.fullName ?: ""
        val email = input.emailAddress ?: ""
        val phoneNumber = input.phoneNumber ?: ""
        val gender = input.gender?.stringToGender() ?: Gender.MALE
        val weight = input.weight ?: 70.0
        val isWeightKgs = WeightUnits.KGS.valueToEnum(input.weightUnit)
        val height = input.height ?: 70.0
        val isHeightInches = HeightUnits.CMS.valueToEnum(input.heightUnit)
        val dob = input.dob ?: ""
        val isRegistered = input.isRegistered ?: false
        val profilePicUrl: String = input.googleProfileUrl ?: when (gender) {
            Gender.MALE -> "https://public-images-profile.s3.ap-south-1.amazonaws.com/man.png"
            Gender.FEMALE -> "https://public-images-profile.s3.ap-south-1.amazonaws.com/business-women.png"
            Gender.OTHERS -> "https://public-images-profile.s3.ap-south-1.amazonaws.com/man.png"
        }

        return User(
            fullName = fullName,
            gender = gender,
            weight = weight,
            weightUnit = isWeightKgs,
            height = height,
            heightUnit = isHeightInches,
            dob = dob,
            emailAddress = email,
            isRegistered = isRegistered,
            profilePicUrl = profilePicUrl
        )
    }
}