package ai.heart.classickbeats.compute

import com.github.psambit9791.jdsp.filter.Chebyshev
import com.github.psambit9791.jdsp.signal.peaks.FindPeak
import com.github.psambit9791.jdsp.transform.Hilbert
import timber.log.Timber
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt


class Filter{

    fun reverseArray(X: DoubleArray): DoubleArray{
        var out = mutableListOf<Double>()
        val N = X.size
        for (i in 0 until N){
            out.add(X[N-i-1])
        }
        return out.toDoubleArray()
    }

    fun chebyBandpass(X: Array<Double>): List<Double>{
        val rf = 5.0
        val cheby = Chebyshev(X.toDoubleArray(), 100.0, rf, 2)
        val fX = cheby.bandPassFilter(4, 0.4, 4.0)
        val fXR = reverseArray(fX)

        val cheby2 = Chebyshev(fXR, 100.0, rf, 2)
        val ffXR = cheby2.bandPassFilter(4, 0.4, 4.0)
        val ffXRR = reverseArray(ffXR)

        return ffXRR.toMutableList()
    }

    fun chebyBandpassSimple(X: Array<Double>): List<Double>{
        val cheby = Chebyshev(X.toDoubleArray(), 100.0, 10.0, 2)
        val y = cheby.bandPassFilter(4, 0.4, 4.0)
        return y.toMutableList()
    }

    fun hilbert(X: Array<Double>): List<Double>{
        val h = Hilbert(X.toDoubleArray())
        h.hilbertTransform()
        h.output
        return (h.amplitudeEnvelope).toMutableList()
    }

    fun peakDetection(X: Array<Double>): Pair<List<Int>, Double> {

        fun sd(data: DoubleArray): Double {
            val mean = data.average()
            return data
                .fold(0.0, { accumulator, next -> accumulator + (next - mean).pow(2.0) })
                .let { sqrt(it / data.size )
                }
        }

        val fp = FindPeak(X.toDoubleArray())
        val out = fp.detectPeaks()

        val peaks = out.peaks
        Timber.i("Size, Peaks: ${peaks.size}, ${Arrays.toString(peaks)}")
        var filteredPeaks = out.filterByProminence(peaks, 0.5, null)
        Timber.i("Size, Prominent Peaks: ${filteredPeaks.size}, ${Arrays.toString(filteredPeaks)}")
//        filteredPeaks = out.filterByWidth(filteredPeaks, 20.0, null)
//        Timber.i("Size, Wide and Prominent Peaks: ${filteredPeaks.size}, ${Arrays.toString(filteredPeaks)}")

        val peaksHeights = mutableListOf<Double>()
        for (i in filteredPeaks){
            peaksHeights.add(X[i])
        }

        val varPeaks = sd(peaksHeights.toDoubleArray()).pow(2.0)
        var quality = varPeaks*((peaks.size - filteredPeaks.size).toDouble())/X.size
        return Pair(filteredPeaks.toMutableList(), quality)
    }
}