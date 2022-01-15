package ai.heart.classickbeats.mapper.input

import ai.heart.classickbeats.mapper.Mapper
import ai.heart.classickbeats.model.MeditationMedia
import ai.heart.classickbeats.model.entity.MeditationEntity
import ai.heart.classickbeats.model.getMeditationLanguage
import ai.heart.classickbeats.model.getWellnessType
import javax.inject.Inject

class MeditationDataMapper @Inject constructor() : Mapper<MeditationEntity, MeditationMedia> {

    override fun map(input: MeditationEntity): MeditationMedia {
        val id = input.id
        val name = input.name ?: ""
        val wellnessType = input.category.getWellnessType()
        val duration = input.duration
        val resourceUrl = input.s3Url
        val language = input.language?.getMeditationLanguage() ?: MeditationMedia.Language.English
        val isGuided = input.guided
        return MeditationMedia(
            id = id,
            name = name,
            wellnessType = wellnessType,
            duration = duration,
            resourceUrl = resourceUrl,
            language = language,
            isGuided = isGuided
        )
    }
}
