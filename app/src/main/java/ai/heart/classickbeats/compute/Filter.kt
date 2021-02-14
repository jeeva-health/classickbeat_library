package ai.heart.classickbeats.compute

import com.github.psambit9791.jdsp.filter.Chebyshev
//import uk.me.berndporr.iirj.ChebyshevII


class Filter{
    fun chebyFilter(X: Array<Double>): List<Double>{
        /*
        From python scipy
        b = array([ 0.08538951, -0.64831602,  2.18417638, -4.26809226,  5.29368476,
            -4.26809226,  2.18417638, -0.64831602,  0.08538951])
        a = array([  1.        ,  -7.46143988,  24.39396243, -45.6437863 ,
            53.46344067, -40.14459053,  18.87179701,  -5.07834891,
            0.59896551])
        */
        val B = doubleArrayOf(0.08538951, -0.64831602,  2.18417638, -4.26809226,  5.29368476,
            -4.26809226,  2.18417638, -0.64831602,  0.08538951)
        val A = doubleArrayOf(1.0       ,  -7.46143988,  24.39396243, -45.6437863 ,
            53.46344067, -40.14459053,  18.87179701,  -5.07834891, 0.59896551)

        val y = Filtfilt.doFiltfilt(B.toCollection(ArrayList()), A.toCollection(ArrayList()), X.toCollection(ArrayList()))


        return y
    }

//    fun chebyBandpass(X: Array<Double>): List<Double>{
//        val cheby = ChebyshevII()
//        cheby.bandPass(4, 100.0, 2.2, 3.6, 10.0)
//        val y = mutableListOf<Double>()
//        for(i in 0 until X.size){
//            y.add(cheby.filter(X[i]))
//        }
//        return y
//    }

    fun chebyBandpass2(X: Array<Double>): List<Double>{
        val cheby = Chebyshev(X.toDoubleArray(), 100.0, 1.0, 2)
        val y = cheby.bandPassFilter(4, 0.4, 4.0)
        return y.toMutableList()
    }

//    fun chebyLowpass(X: Array<Double>): List<Double>{
//        val cheby = ChebyshevII()
//        cheby.lowPass(4, 100.0, 0.4, 1.0)
//        val y = mutableListOf<Double>()
//        for(i in 0 until X.size){
//            y.add(cheby.filter(X[i]))
//        }
//        return y
//    }
}