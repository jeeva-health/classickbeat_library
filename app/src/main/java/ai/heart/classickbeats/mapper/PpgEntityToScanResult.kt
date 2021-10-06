package ai.heart.classickbeats.mapper

import ai.heart.classickbeats.model.BioAge
import ai.heart.classickbeats.model.PPGData
import ai.heart.classickbeats.model.StressResult
import ai.heart.classickbeats.model.entity.PPGEntity
import ai.heart.classickbeats.shared.util.toDate
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
                userAge < bioAge.startRange -> -1
                userAge > bioAge.endRange -> 1
                else -> 0
            }
        } else {
            0
        }

        val isActive = ppgEntity.sedRatioLog ?: 0f < 0

        return PPGData.ScanResult(
            bpm = ppgEntity.hr ?: 0.0f,
            aFib = "Not Detected",
            quality = ppgEntity.quality ?: 0.0f,
            ageBin = ppgEntity.bAgeBin ?: 0,
            bioAgeResult = bioAgeResult,
            activeStar = 6 - (ppgEntity.sedStars ?: 0),
            sdnn = ppgEntity.sdnn ?: 0.0f,
            pnn50 = ppgEntity.pnn50 ?: 0.0f,
            rmssd = ppgEntity.rmssd ?: 0.0f,
            isActive = isActive,
            stress = StressResult(stressResult = ppgEntity.stressLevel ?: 0, dataCount = 0),
            filteredRMean = ppgEntity.filteredRMeans ?: emptyList(),
            timeStamp = ppgEntity.timeStamp?.toDate() ?: Date()
        )
    }
}