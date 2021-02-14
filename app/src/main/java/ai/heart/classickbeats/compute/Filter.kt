package ai.heart.classickbeats.compute

import com.github.psambit9791.jdsp.filter.Chebyshev
import com.github.psambit9791.jdsp.signal.peaks.FindPeak
import com.github.psambit9791.jdsp.transform.Hilbert


class Filter{

    fun chebyBandpass(X: Array<Double>): List<Double>{
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

    fun peakDetection(X: Array<Double>): List<Int>{
        val fp = FindPeak(X.toDoubleArray())
        val out = fp.detectPeaks()

        val peaks = out.peaks
        var filteredPeaks = out.filterByProminence(peaks, 1.0, null)
        filteredPeaks = out.filterByWidth(filteredPeaks, 20.0, null)
        return filteredPeaks.toMutableList()
    }
}