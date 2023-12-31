package ai.heart.classickbeats.compute

import com.github.psambit9791.jdsp.signal.peaks.FindPeak
import com.github.psambit9791.jdsp.transform.Hilbert
import timber.log.Timber
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

object Filter {

    fun hilbert(X: List<Double>): DoubleArray {
        Timber.i("TrackTime: Hilbert starting (fn)")
        val h = Hilbert(X.toDoubleArray())
        h.hilbertTransform()
        h.output
        Timber.i("TrackTime: Hilbert computed (fn)")
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
//        Timber.i("TrackTime: Peaks detected (fn)")

        val peaks = out.peaks
        Timber.i("Size, Peaks: ${peaks.size}, ${Arrays.toString(peaks)}")
        val filteredPeaks = out.filterByProminence(peaks, 0.4, null)
        Timber.i("Size, Prominent Peaks: ${filteredPeaks.size}, ${Arrays.toString(filteredPeaks)}")
//        filteredPeaks = out.filterByWidth(filteredPeaks, 20.0, null)
        Timber.i("Size, Wide and Prominent Peaks: ${filteredPeaks.size}, ${Arrays.toString(filteredPeaks)}")
        Timber.i("TrackTime: Prominent Peaks detected (fn)")

        val peaksHeights = mutableListOf<Double>()
        for (i in filteredPeaks) {
            peaksHeights.add(X[i])
        }

        val varPeaks = sd(peaksHeights.toDoubleArray()).pow(2.0)
        val quality = varPeaks * ((peaks.size - filteredPeaks.size).toDouble()) / X.size
        return Pair(filteredPeaks.toMutableList(), quality)
    }
}
