package ai.heart.classickbeats.compute

import com.github.psambit9791.jdsp.filter.Chebyshev
import com.github.psambit9791.jdsp.signal.peaks.FindPeak
import com.github.psambit9791.jdsp.transform.Hilbert
import timber.log.Timber
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt


class Filter{
    fun filtfilt(X: Array<Double>): List<Double>{
        /*
        From python scipy
        b = array([ 0.08538951, -0.64831602,  2.18417638, -4.26809226,  5.29368476,
            -4.26809226,  2.18417638, -0.64831602,  0.08538951])
        a = array([  1.        ,  -7.46143988,  24.39396243, -45.6437863 ,
            53.46344067, -40.14459053,  18.87179701,  -5.07834891,
            0.59896551])
        */
//        val B = ArrayList<Double>()
//        val A = ArrayList<Double>()
        val B = doubleArrayOf(0.08538951, -0.64831602,  2.18417638, -4.26809226,  5.29368476,
            -4.26809226,  2.18417638, -0.64831602,  0.08538951)
        val A = doubleArrayOf(1.0       ,  -7.46143988,  24.39396243, -45.6437863 ,
            53.46344067, -40.14459053,  18.87179701,  -5.07834891, 0.59896551)

        val y = Filtfilt.doFiltfilt(B.toCollection(ArrayList()), A.toCollection(ArrayList()), X.toCollection(ArrayList()))
        Timber.i("Size of Filter output: ${y.size}")


        return y
    }

    fun reverseArray(X: DoubleArray): DoubleArray{
        var out = mutableListOf<Double>()
        val N = X.size
        for (i in 0 until N){
            out.add(X[N-i-1])
        }
        return out.toDoubleArray()
    }

    fun chebyBandpass(X: Array<Double>): List<Double>{
        val rf = 0.5
        val cheby = Chebyshev(X.toDoubleArray(), 100.0, rf, 2)
        val fX = cheby.bandPassFilter(4, 0.4, 4.0)
        val fXR = reverseArray(fX)

        val cheby2 = Chebyshev(fXR, 100.0, rf, 2)
        val ffXR = cheby2.bandPassFilter(4, 0.4, 4.0)
        val ffXRR = reverseArray(ffXR)

        return ffXRR.toMutableList()
    }

    fun chebyBandpassSimple(X: Array<Double>): List<Double>{
        val cheby = Chebyshev(X.toDoubleArray(), 100.0, 1.0, 2)
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
        var filteredPeaks = out.filterByProminence(peaks, 0.4, null)
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