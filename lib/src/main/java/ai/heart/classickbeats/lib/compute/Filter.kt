package ai.heart.classickbeats.lib.compute

import com.github.psambit9791.jdsp.filter.Chebyshev
import com.github.psambit9791.jdsp.signal.peaks.FindPeak
import com.github.psambit9791.jdsp.transform.Hilbert
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt


object Filter {

    fun reverseArray(X: DoubleArray): DoubleArray {
        var out = mutableListOf<Double>()
        val N = X.size
        for (i in 0 until N) {
            out.add(X[N - i - 1])
        }
        return out.toDoubleArray()
    }

    fun chebyBandpass(X: Array<Double>): List<Double> {
        val rf = 0.5
        val cheby = Chebyshev(X.toDoubleArray(), 100.0, rf, 2)
        val fX = cheby.bandPassFilter(4, 0.4, 4.0)
        val fXR = reverseArray(fX)

        val cheby2 = Chebyshev(fXR, 100.0, rf, 2)
        val ffXR = cheby2.bandPassFilter(4, 0.4, 4.0)
        val ffXRR = reverseArray(ffXR)

        return ffXRR.toMutableList()
    }

    fun chebyBandpassSimple(X: Array<Double>): List<Double> {
        val cheby = Chebyshev(X.toDoubleArray(), 100.0, 1.0, 2)
        val y = cheby.bandPassFilter(4, 0.4, 4.0)
        return y.toMutableList()
    }

    fun hilbert(X: List<Double>): DoubleArray {
        val h = Hilbert(X.toDoubleArray())
        h.hilbertTransform()
        h.output
        return h.amplitudeEnvelope
    }

    private fun sd(data: DoubleArray): Double {
        val mean = data.average()
        return data
            .fold(0.0, { accumulator, next -> accumulator + (next - mean).pow(2.0) })
            .let {
                sqrt(it / data.size)
            }
    }

    fun peakDetection(X: List<Double>): Pair<List<Int>, Double> {
        val fp = FindPeak(X.toDoubleArray())
        val out = fp.detectPeaks()

        val peaks = out.peaks
        val filteredPeaks = out.filterByProminence(peaks, 0.4, null)

        val peaksHeights = mutableListOf<Double>()
        for (i in filteredPeaks) {
            peaksHeights.add(X[i])
        }

        val varPeaks = sd(peaksHeights.toDoubleArray()).pow(2.0)
        val quality = varPeaks * ((peaks.size - filteredPeaks.size).toDouble()) / X.size
        return Pair(filteredPeaks.toMutableList(), quality)
    }
}