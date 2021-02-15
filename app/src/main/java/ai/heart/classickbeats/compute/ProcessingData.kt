package ai.heart.classickbeats.compute
import org.apache.commons.math3.analysis.interpolation.AkimaSplineInterpolator

import kotlin.math.sqrt
import kotlin.math.pow

class ProcessingData{

    fun interpolate(xArray: Array<Int>, yArray: Array<Double>, duration: Int): List<Double> {
        val akimaSplineInterpolator = AkimaSplineInterpolator()
        val x0 = xArray[0]
        val pXDouble = xArray.map { (it - x0).toDouble() }

        val polynomialFunction =
            akimaSplineInterpolator.interpolate(pXDouble.toDoubleArray(), yArray.toDoubleArray())
        val size = duration * 100

        val xMax = pXDouble.maxOrNull()!!
        val inputList = (0 until size).map { it * xMax / size }
        val outputList = mutableListOf<Double>()
        for (i in inputList) {
            outputList.add(polynomialFunction.value(i))
        }
        return outputList
    }

    fun movAvg(X: Array<Double>, window: Int): List<Double>{
        val movingWindow = mutableListOf<Double>()
        val y = mutableListOf<Double>()
        for (i in 0 until X.size) {
            if (i < window) {
                movingWindow.add(X[i])
            } else {
                movingWindow.removeAt(0)
                movingWindow.add(X[i])
                y.add(movingWindow.average())
            }
        }
        return y
    }

    fun centering(X: Array<Double>, movAvg: Array<Double>, window: Int): List<Double>{
        val Xlist = X.toMutableList()
        for (i in 0 until window){
            Xlist.removeAt(0)
        }
        assert(Xlist.size == movAvg.size)
        val differ = Xlist.zip(movAvg, Double::minus)
        return differ
    }

    fun leveling(X: Array<Double>, movAvg: Array<Double>, window: Int): List<Double>{
        val Xlist = X.toMutableList()
        for (i in 0 until window){
            Xlist.removeAt(0)
        }
        assert(Xlist.size == movAvg.size)
        val differ = Xlist.zip(movAvg, Double::div)
        return differ
    }

    fun heartRateAndHRV(peaks: List<Int>): Pair<Double, Double>{

        fun sd(data: DoubleArray): Double {
            val mean = data.average()
            return data
                .fold(0.0, { accumulator, next -> accumulator + (next - mean).pow(2.0) })
                .let { sqrt(it / data.size )
                }
        }

        val time = (0 until 3000).toList()
        val ibiList = mutableListOf<Double>() //Time in milliseconds

        for(i in 0 until peaks.size-1){
            ibiList.add((time[peaks[i+1]] - time[peaks[i]])*10.0)
        }
        val ibi = ibiList.average()
        // Add a condition to keep ibi from 0.6*ibi < t < 1.4*ibi
        val bpm = (60*1000.0)/ibi
        val SDNN = sd(ibiList.toDoubleArray())
        return Pair(bpm, SDNN)
    }
}