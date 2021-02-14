package ai.heart.classickbeats.compute

class ProcessingData{
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
}