package ai.heart.classickbeats.compute

import ai.heart.classickbeats.model.Constants.SCAN_DURATION
import org.apache.commons.math3.analysis.interpolation.AkimaSplineInterpolator
import timber.log.Timber
import kotlin.math.absoluteValue
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt


object ProcessingData {

    fun sd(data: DoubleArray): Double {
        val mean = data.average()
        return data
            .fold(0.0, { accumulator, next -> accumulator + (next - mean).pow(2.0) })
            .let {
                sqrt(it / data.size)
            }
    }

    fun runningMovAvg(windowSize: Int, dataList: List<Double>): Double? {
        val average = if (dataList.size >= windowSize) {
            dataList.takeLast(windowSize).average()
        } else {
            null
        }
        return average
    }

    fun computeLeveledSignal(
        timeList: List<Int>,
        centeredSignalList: List<Double>,
        smallWindowOffset: Int = 2,
        largeWindowOffset: Int = 15,
        windowSize: Int = 101,
        fInterpolate: Double = 100.0
    ): List<Double> {

        val time = timeList.subList(
            smallWindowOffset + largeWindowOffset,
            timeList.size - largeWindowOffset
        )

        assert(time.size == centeredSignalList.size)

        // TODO: Vipul remove interpolated list
        val interpolatedList = interpolate(
            time,
            centeredSignalList,
            fInterpolate
        )

        val envelope = Filter.hilbert(interpolatedList)
        val envelopeAverage = movAvg(envelope, windowSize)

        val leveledSignal = leveling(
            interpolatedList,
            envelopeAverage,
            windowSize
        )
        return leveledSignal
    }

    fun calculateIbiListAndQuality(
        inputSignal: List<Double>,
        fInterpolate: Double
    ): Pair<List<Double>, Double> {
        val (peaks, quality) = Filter.peakDetection(inputSignal)
        val ibiList = computeIBI(peaks, fInterpolate)
        return Pair(ibiList, quality)
    }

    fun calculatePulseStats(ibiList: List<Double>): List<Double> {

        val ibiListMutable = ibiList.toMutableList()

        var ibiMedian = median(ibiListMutable)
        var i = 0
        while (i < ibiListMutable.size - 1) {
            if (ibiListMutable[i] + ibiListMutable[i + 1] < 1.5 * ibiMedian) {
                ibiListMutable[i] = ibiListMutable[i] + ibiListMutable[i + 1]
                ibiListMutable.removeAt(i + 1)
            }
            i++
        }

        ibiMedian = median(ibiListMutable)
        i = 0
        while (i < ibiListMutable.size - 1) {
            if (ibiListMutable[i] + ibiListMutable[i + 1] < 1.5 * ibiMedian) {
                ibiListMutable[i] = ibiListMutable[i] + ibiListMutable[i + 1]
                ibiListMutable.removeAt(i + 1)
            }
            i++
        }

        ibiMedian = median(ibiListMutable)
        val filteredIbiList = ibiListMutable.filter { it > 0.8 * ibiMedian && it < 1.2 * ibiMedian }
        val ibiAvg2 = filteredIbiList.average()

        val SDNN = sd(filteredIbiList.toDoubleArray())
        var rmssd = 0.0
        var nn50 = 0
        val ibiSize = filteredIbiList.size - 1
        for (i in 0 until ibiSize) {
            val diffRR = (filteredIbiList[i] - filteredIbiList[i + 1]).absoluteValue
            rmssd += diffRR.pow(2)
            if (diffRR >= 50) {
                nn50 += 1
            }
        }
        rmssd = sqrt(rmssd / ibiSize)
        val pnn50 = (100.0 * nn50) / ibiSize

        return listOf(ibiAvg2, SDNN, rmssd, pnn50, ln(rmssd))
    }

    fun qualityPercent(quality: Double): Double {
        /*
        The following block defines the main idea of qualityPercent:
            quality <= 1e-5 -> "PERFECT Quality Recording, Good job!"
            quality <= 1e-4 -> "Good Quality Recording, Good job!"
            quality <= 1e-3 -> "Decent Quality Recording!"
            ------ Anything > 0.001 is rejected --------------------
            quality <= 1e-2 -> "Poor Quality Recording. Please record again!"
            else -> "Extremely poor signal quality. Please record again!"
         */
        val highQualThres = 95.0
        val lowQualThres = 0.0
        val midQualThres = 70.0
        val lowQual = 2.0
        val midQual = 3.0
        val highQual = 5.0

        var qualityPercent = 0.0
        if (quality == 0.0) {
            qualityPercent = 100.0
        } else {
            qualityPercent = -1.0 * kotlin.math.log10(quality)
            if (qualityPercent >= highQual) {
                qualityPercent = 100 - (100 - highQualThres) * ((highQual / qualityPercent).pow(2))
            } else if (qualityPercent >= midQual) {
                qualityPercent =
                    (highQualThres - midQualThres) * (qualityPercent - midQual) + midQualThres
            } else if (qualityPercent >= lowQual) {
                qualityPercent =
                    (midQualThres - lowQualThres) * (qualityPercent - lowQual) + lowQualThres
            } else {
                qualityPercent = lowQualThres
            }
        }
        Timber.i("QualityPERCENT: $qualityPercent")
        return qualityPercent
    }

    private fun median(l: List<Double>) =
        l.sorted().let { (it[it.size / 2] + it[(it.size - 1) / 2]) / 2 }

    private fun interpolate(xArray: List<Int>, yArray: List<Double>, f: Double): List<Double> {
        Timber.i("Sizes X, Y in interpolator $xArray.size, $yArray.size")
        val timePerSample = 1000.0 / f
        val akimaSplineInterpolator = AkimaSplineInterpolator()
        val x0 = xArray[0]
        val pXDouble = xArray.map { (it - x0).toDouble() }
        val xMax = pXDouble.last()
        Timber.i("Max time recorded: $xMax")
        val size = (xMax / timePerSample).toInt()

        val polynomialFunction =
            akimaSplineInterpolator.interpolate(pXDouble.toDoubleArray(), yArray.toDoubleArray())

        val inputList = (0 until size).map { it * timePerSample }
        val outputList = inputList.map { polynomialFunction.value(it) }
        Timber.i("Interpolation done! Output size: ${outputList.size}")
        return outputList
    }

    fun spikeRemover(X: Array<Double>): List<Double> {
        val windowSize = 50
        val maxMinWindow = X.toMutableList().windowed(
            size = windowSize,
            step = windowSize
        ) { window -> window.maxOrNull()!! - window.minOrNull()!! }
        val medianAmplitude = median(maxMinWindow)
        val outlierWindowIndex =
            maxMinWindow.mapIndexed { index, d -> if (d > 3 * medianAmplitude) index else -1 }
                .filter { it != -1 }
        if (outlierWindowIndex.isEmpty())
            return X.toList()
        val outlierDataIndex = mutableListOf<Int>()
        outlierWindowIndex.forEach {
            val outlierSubWindow = it * windowSize until it * windowSize + windowSize
            outlierDataIndex.addAll(outlierSubWindow.toList())
        }

        val filteredDataIndex =
            (X.indices).toList().filter { !outlierDataIndex.contains(it) }

        val akimaSplineInterpolator = AkimaSplineInterpolator()
        val polynomialFunction =
            akimaSplineInterpolator.interpolate(
                filteredDataIndex.map { it.toDouble() }.toDoubleArray(),
                X.toList().filterIndexed { index, d -> filteredDataIndex.contains(index) }
                    .toDoubleArray()
            )

        val withoutSpikesData = mutableListOf<Double>()
        for (i in X.indices) {
            withoutSpikesData.add(polynomialFunction.value(i.toDouble()))
        }

        return withoutSpikesData
    }

    // TODO: modify it to remove unnecessary list to array conversion
    private fun movAvg(X: DoubleArray, window_size: Int): Array<Double> {
        return X.toList().windowed(
            size = window_size,
            step = 1,
            partialWindows = false
        ) { window -> window.average() }.toTypedArray()
    }

    private fun leveling(X: List<Double>, movAvg: Array<Double>, window_size: Int): List<Double> {
        val offset = (window_size - 1) / 2
        return X.subList(offset, X.size - offset).zip(movAvg, Double::div)
    }

    private fun computeIBI(peaks: List<Int>, f: Double): MutableList<Double> {

        val time = (0 until 100 * SCAN_DURATION).toList()
        val ibiList = mutableListOf<Double>() //Time in milliseconds

        val timePerSample = 1000.0 / f
        for (i in 0 until peaks.size - 1) {
            ibiList.add((time[peaks[i + 1]] - time[peaks[i]]) * timePerSample)
        }
        return ibiList
    }
}