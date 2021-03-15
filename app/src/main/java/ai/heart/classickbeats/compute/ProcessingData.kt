package ai.heart.classickbeats.compute

import org.apache.commons.math3.analysis.interpolation.AkimaSplineInterpolator
import timber.log.Timber
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt


class ProcessingData {

    fun median(l: List<Double>) = l.sorted().let { (it[it.size / 2] + it[(it.size - 1) / 2]) / 2 }

    fun interpolate(xArray: Array<Int>, yArray: Array<Double>): List<Double> {
        val akimaSplineInterpolator = AkimaSplineInterpolator()
        val x0 = xArray[0]
        val pXDouble = xArray.map { (it - x0).toDouble() }

        val polynomialFunction =
            akimaSplineInterpolator.interpolate(pXDouble.toDoubleArray(), yArray.toDoubleArray())
        val xMax = pXDouble.maxOrNull()!!
        val size = (xMax / 10).toInt()

        Timber.i("Max time recorded: $xMax")
        val inputList = (0 until size).map { it * 10.0 }
        val outputList = mutableListOf<Double>()
        for (i in inputList) {
            outputList.add(polynomialFunction.value(i))
        }
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
            maxMinWindow.mapIndexed { index, d -> if (d > 4 * medianAmplitude) index else -1 }
                .filter { it != -1 }
        val outlierDataIndex = mutableListOf<Int>()
        outlierWindowIndex.forEach {
            val outlierSubWindow = it * windowSize until it * windowSize + windowSize
            outlierDataIndex.addAll(outlierSubWindow.toList())
        }

        val akimaSplineInterpolator = AkimaSplineInterpolator()
        val polynomialFunction =
            akimaSplineInterpolator.interpolate(pXDouble.toDoubleArray(), yArray.toDoubleArray())

        return X.toMutableList().filterIndexed { index, d -> !outlierDataIndex.contains(index)}
    }

    fun movAvg(X: Array<Double>, window_size: Int): List<Double> {
        return X.toMutableList().windowed(
            size = window_size,
            step = 1,
            partialWindows = false
        ) { window -> window.average() }
//        val movingWindow = mutableListOf<Double>()
//        val y = mutableListOf<Double>()
//        for (i in 0 until X.size) {
//            if (i < window) {
//                movingWindow.add(X[i])
//            } else {
//                movingWindow.removeAt(0)
//                movingWindow.add(X[i])
//            }
//            y.add(movingWindow.average())
//
//        }
    }

    fun centering(X: Array<Double>, movAvg: Array<Double>, window_size: Int): List<Double> {
        val offset = (window_size - 1) / 2
        val X_reqd = X.copyOfRange(offset, X.size - offset)
        val Xlist = X_reqd.toMutableList()
        assert(Xlist.size == movAvg.size)
        val differ = Xlist.zip(movAvg, Double::minus)
        return differ
    }

    fun leveling(X: Array<Double>, movAvg: Array<Double>, window_size: Int): List<Double> {
        val offset = (window_size - 1) / 2
        val X_reqd = X.copyOfRange(offset, X.size - offset)
        val Xlist = X_reqd.toMutableList()
        assert(Xlist.size == movAvg.size)
        val differ = Xlist.zip(movAvg, Double::div)
        return differ
    }

    fun sd(data: DoubleArray): Double {
        val mean = data.average()
        return data
            .fold(0.0, { accumulator, next -> accumulator + (next - mean).pow(2.0) })
            .let {
                sqrt(it / data.size)
            }
    }

    fun heartRateAndHRV(peaks: List<Int>, scanDuration: Int): Pair<Double, Double> {

        val time = (0 until 100 * scanDuration).toList()
        val ibiList = mutableListOf<Double>() //Time in milliseconds

        for (i in 0 until peaks.size - 1) {
            ibiList.add((time[peaks[i + 1]] - time[peaks[i]]) * 10.0)
        }
        val ibiMedian = median(ibiList)
        Timber.i("Size, Median, ibiList: ${ibiList.size}, $ibiMedian, ${Arrays.toString(ibiList.toDoubleArray())}")
        val filteredIbiList = ibiList.filter { it > 0.8 * ibiMedian && it < 1.2 * ibiMedian }
        val ibiAvg2 = filteredIbiList.average()
        Timber.i(
            "Size, Avg, filteredIbiList: ${filteredIbiList.size}, $ibiAvg2, " +
                    "${Arrays.toString(filteredIbiList.toDoubleArray())}"
        )
//        val rejectedIntervals = ibiList.filter { it <= 0.6 * ibiAvg && it >= 1.4 * ibiAvg }
//        Timber.i("Rejected Intervals Size: ${rejectedIntervals.size}"
        val bpm = (60 * 1000.0) / ibiAvg2
        val SDNN = sd(filteredIbiList.toDoubleArray())
        return Pair(bpm, SDNN)
    }
}