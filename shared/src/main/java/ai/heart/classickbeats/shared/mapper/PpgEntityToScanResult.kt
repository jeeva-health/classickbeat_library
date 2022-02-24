package ai.heart.classickbeats.shared.mapper

import ai.heart.classickbeats.model.BioAge
import ai.heart.classickbeats.model.PPGData
import ai.heart.classickbeats.model.StressResult
import ai.heart.classickbeats.model.entity.PPGEntity
import ai.heart.classickbeats.model.toLifestyleCategory
import ai.heart.classickbeats.shared.util.toPPGDate
import java.util.*

object PpgEntityToScanResult {

    fun map(
        userAge: Int,
        ppgEntity: PPGEntity
    ): PPGData.ScanResult {
        val bAgeBin = ppgEntity.bAgeBin ?: 0
        val bioAge = BioAge.values()[bAgeBin]
        val bioAgeResult = if (userAge != -1) {
            when {
                userAge < bioAge.startRange -> 1
                userAge > bioAge.endRange -> -1
                else -> 0
            }
        } else {
            0
        }
        val activeStars = ppgEntity.activeStars ?: 2
        val isActive = activeStars > 3

        return PPGData.ScanResult(
            bpm = ppgEntity.hr ?: 0.0f,
            aFib = "Not Detected",
            quality = ppgEntity.quality ?: 0.0f,
            ageBin = ppgEntity.bAgeBin ?: 0,
            bioAgeResult = bioAgeResult,
            activeStar = activeStars,
            lifestyleCategory = ppgEntity.lifeStyleCategory?.toLifestyleCategory()
                ?: PPGData.ScanResult.LifestyleCategory.ModeratelyActive,
            sdnn = ppgEntity.sdnn ?: 0.0f,
            pnn50 = ppgEntity.pnn50 ?: 0.0f,
            rmssd = ppgEntity.rmssd ?: 0.0f,
            isActive = isActive,
            stress = StressResult(
                stressResult = ppgEntity.stressLevel ?: 0,
                dataCount = ppgEntity.ppgCount ?: 0,
                distinctDataCount = ppgEntity.ppgDistinctDays ?: 0
            ),
            filteredRMean = ppgEntity.filteredRMeans ?: emptyList(),
            timeStamp = ppgEntity.timeStamp?.toPPGDate() ?: Date(),
            isBaselineSet = ppgEntity.isBaselineSet ?: false,
        )
    }
}
