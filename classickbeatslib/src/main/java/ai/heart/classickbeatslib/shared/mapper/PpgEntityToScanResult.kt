package ai.heart.classickbeatslib.shared.mapper

import ai.heart.classickbeatslib.model.PPGData
import ai.heart.classickbeatslib.model.StressResult
import ai.heart.classickbeatslib.model.entity.PPGEntity
import ai.heart.classickbeatslib.model.toHeartAgeClassification
import ai.heart.classickbeatslib.model.toLifestyleCategory
import ai.heart.classickbeatslib.shared.util.toPPGDate
import java.util.*

object PpgEntityToScanResult {

    fun map(
        ppgEntity: PPGEntity
    ): PPGData.ScanResult {
        val activeStars = ppgEntity.activeStars ?: 2
        val isActive = activeStars > 3

        return PPGData.ScanResult(
            bpm = ppgEntity.hr ?: 0.0f,
            aFib = "Not Detected",
            quality = ppgEntity.quality ?: 0.0f,
            ageBin = ppgEntity.bAgeBin ?: 0,
            heartAgeClassification = ppgEntity.heartAgeClassification?.toHeartAgeClassification()
                ?: PPGData.ScanResult.HeartAgeClassification.Similar,
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
