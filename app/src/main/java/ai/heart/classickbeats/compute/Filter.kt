package ai.heart.classickbeats.compute

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
//        val B = ArrayList<Double>()
//        val A = ArrayList<Double>()
        val B = doubleArrayOf(0.08538951, -0.64831602,  2.18417638, -4.26809226,  5.29368476,
            -4.26809226,  2.18417638, -0.64831602,  0.08538951)
        val A = doubleArrayOf(1.0       ,  -7.46143988,  24.39396243, -45.6437863 ,
            53.46344067, -40.14459053,  18.87179701,  -5.07834891, 0.59896551)

        val a = doubleArrayOf(0.0,1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0)
        val b = doubleArrayOf(0.0,1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0)

        val y = Filtfilt.doFiltfilt(B.toCollection(ArrayList()), A.toCollection(ArrayList()), X.toCollection(ArrayList()))


        return y
    }
}