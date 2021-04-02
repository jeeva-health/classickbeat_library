package ai.heart.classickbeats.compute

import org.apache.commons.math3.distribution.NormalDistribution
import timber.log.Timber
import java.util.*


class MAPmodeling {
    private fun gaussianAgePrior(cAge: Double, meanDelta: Double = 0.0, std: Double = 10.0): List<Double>{
        val bAgeMean = cAge + meanDelta
        val normal = NormalDistribution(bAgeMean, std)
        var binProb = mutableListOf<Double>()
        val seqAges = listOf<Double>(25.0, 35.0, 45.0, 55.0, 65.0, 75.0)
        for (i in 0 until seqAges.size-1){
            binProb.add(normal.probability(seqAges[i], seqAges[i+1]))
        }
        binProb = binProb.map{it/binProb.sum()}.toMutableList()
        Timber.i("binProbsPrior: ${Arrays.toString(binProb.toDoubleArray())}")
        return binProb
    }

    private fun gaussianPosterior(meanNN: Double, sdnn: Double, rmssd: Double, pnn50: Double, gender: Int): MutableList<Double>{
        //gender = 0 corresponds to male
        var meanNNMeanArr = listOf<Double>()
        var meanNNStdArr = listOf<Double>()
        var sdnnMeanArr = listOf<Double>()
        var sdnnStdArr = listOf<Double>()
        var rmssdMeanArr = listOf<Double>()
        var rmssdStdArr = listOf<Double>()
        var pnnMeanArr = listOf<Double>()
        var pnnStdArr = listOf<Double>()

        if (gender == 0){
            meanNNMeanArr = listOf<Double>(939.0, 925.0, 923.0, 904.0, 906.0)
            meanNNStdArr = listOf<Double>(129.0, 138.0, 134.0, 123.0, 123.0)

            sdnnMeanArr = listOf<Double>(50.0, 44.6, 36.8, 32.8, 29.6)
            sdnnStdArr = listOf<Double>(20.9, 16.8, 14.6, 14.7, 13.2)

            rmssdMeanArr = listOf<Double>(39.7, 32.0, 23.0, 19.9, 19.1)
            rmssdStdArr = listOf<Double>(19.9, 16.5, 10.9, 11.1, 10.7)

            pnnMeanArr = listOf<Double>(20.0, 13.0, 6.0, 4.0, 4.0)
            pnnStdArr = listOf<Double>(17.0, 15.0, 8.0, 7.0, 7.0)
        } else {
            meanNNMeanArr = listOf<Double>(900.0, 903.0, 903.0, 868.0, 873.0)
            meanNNStdArr = listOf<Double>(116.0, 122.0, 109.0, 118.0, 110.0)

            sdnnMeanArr = listOf<Double>(48.7, 45.4, 36.9, 30.6, 27.8)
            sdnnStdArr = listOf<Double>(19.0, 20.5, 13.8, 12.4, 11.8)

            rmssdMeanArr = listOf<Double>(42.9, 35.4, 26.3, 21.4, 19.1)
            rmssdStdArr = listOf<Double>(22.8, 18.5, 13.6, 11.9, 11.8)

            pnnMeanArr = listOf<Double>(23.0, 16.0, 8.0, 5.0, 4.0)
            pnnStdArr = listOf<Double>(20.0, 17.0, 12.0, 8.0, 6.0)
        }

        val output = mutableListOf<Double>()

        val numBins = meanNNMeanArr.size
        var normal = NormalDistribution(0.0, 1.0)
        for (i in 0 until numBins){
            normal = NormalDistribution(meanNNMeanArr[i], meanNNStdArr[i])
            val meanNNProb = normal.density(meanNN)

            normal = NormalDistribution(sdnnMeanArr[i], sdnnStdArr[i])
            val sdnnProb = normal.density(sdnn)

            normal = NormalDistribution(rmssdMeanArr[i], rmssdStdArr[i])
            val rmssdProb = normal.density(rmssd)

            normal = NormalDistribution(pnnMeanArr[i], pnnStdArr[i])
            val pnnProb = normal.density(pnn50)
            output.add(meanNNProb * sdnnProb * rmssdProb * pnnProb)
        }
        Timber.i("binProbsPosterior: ${Arrays.toString(output.toDoubleArray())}")
        return output
    }

    fun bAgePrediction(cAge: Double, gender: Int, meanNN: Double, sdnn: Double, rmssd: Double, pnn50: Double): List<Double> {
        Timber.i("Testing")
        val priorProb = gaussianAgePrior(cAge)
        val postProb = gaussianPosterior(meanNN, sdnn, rmssd, pnn50, gender)
        var binProbsMAP = priorProb.zip(postProb){ a, b -> a * b }
        binProbsMAP = binProbsMAP.map{it/binProbsMAP.sum()}
        return binProbsMAP
    }
}