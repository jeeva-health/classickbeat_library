package ai.heart.classickbeats.lib.process

import ai.heart.classickbeats.lib.compute.MAPmodeling
import ai.heart.classickbeats.lib.compute.ProcessingData
import ai.heart.classickbeats.lib.model.CameraReading

class ProcessImage() {

    @Volatile
    var isProcessing: Boolean = false

    private var fps = 30
    val fInterp = 100.0

    // Keep window sizes odd
    val smallWindow = fps / 10
    val largeWindow = fps + 1
    val offset = (largeWindow + smallWindow - 1) / 2

    var timeListSplitSize: Int = 0
    var centeredSignalSplitSize: Int = 0

    private var imageCounter = 0
    private var badImageCounter = 0

    private val mean1List = mutableListOf<Double>()
    private val mean2List = mutableListOf<Double>()
    private val mean3List = mutableListOf<Double>()
    val centeredSignal = mutableListOf<Double>()
    val timeList = mutableListOf<Int>()

    private val movAvgSmall = mutableListOf<Double>()
    private val movAvgLarge = mutableListOf<Double>()

    private val ibiList = mutableListOf<Double>()
    private val qualityList = mutableListOf<Double>()
    private val leveledSignalList = mutableListOf<List<Double>>()

    fun processImage(cameraReading: CameraReading) {
        if (isProcessing) {
            imageCounter++
            if (imageCounter >= fps * 1) {
                cameraReading.apply {
                    if (green / red > 0.5 || blue / red > 0.5) {
                        badImageCounter++
                    } else {
                        badImageCounter = 0
                    }
                    if (badImageCounter >= 45) {
                    }
                    addFrameDataToList(cameraReading)
                    calculateCenteredSignal()
                }
            }
        }
    }

    fun addFrameDataToList(cameraReading: CameraReading) {
        val red = cameraReading.red
        val green = cameraReading.green
        val blue = cameraReading.blue
        val timeStamp = cameraReading.timeStamp

        mean1List.add(red)
        mean2List.add(green)
        mean3List.add(blue)
        timeList.add(timeStamp)
    }

    fun calculateCenteredSignal() {
        val smallAvg = ProcessingData.runningMovAvg(
            smallWindow,
            mean1List
        )
        smallAvg?.let { movAvgSmall.add(it) }

        val largeAvg = ProcessingData.runningMovAvg(
            largeWindow,
            movAvgSmall
        )
        largeAvg?.let { movAvgLarge.add(it) }

        val largeWindowOffset = (largeWindow - 1) / 2
        if (movAvgSmall.size >= largeWindow) {
            val x = -1.0 * (movAvgSmall[movAvgSmall.size - largeWindowOffset] - movAvgLarge.last())
            centeredSignal.add(x)
        }
    }

    fun calculateResultSplit(timeList: List<Int>, centeredSignalList: List<Double>) {
            val windowSize = 101

            timeListSplitSize = timeList.size
            centeredSignalSplitSize = centeredSignalList.size

            val smallWindowOffset = smallWindow - 1
            val largeWindowOffset = (largeWindow - 1) / 2
            val leveledSignal = ProcessingData.computeLeveledSignal(
                timeList = timeList,
                centeredSignalList = centeredSignalList,
                smallWindowOffset = smallWindowOffset,
                largeWindowOffset = largeWindowOffset,
                windowSize = windowSize
            )
            val (_ibiList, _quality) = ProcessingData.calculateIbiListAndQuality(
                leveledSignal,
                fInterp
            )

            leveledSignalList.add(leveledSignal)
            ibiList.addAll(_ibiList)
            qualityList.add(_quality)
        }

    private fun calculateSplitCombinedResult() {
        val (meanNN, sdnn, rmssd, pnn50, ln) = ProcessingData.calculatePulseStats(ibiList)

        val qualityPercent = ProcessingData.qualityPercent(qualityList[0])

        val bpm = (60 * 1000.0) / meanNN

        val mapModeling = MAPmodeling()



        clearGlobalData()
    }

    fun endScanHandling() {
            val timeOffset = smallWindow + largeWindow - 2
            val timeListSize = timeList.size
            val centeredSignalSize = centeredSignal.size
            calculateResultSplit(
                timeList.subList(timeListSplitSize - timeOffset, timeListSize).toList(),
                centeredSignal.subList(centeredSignalSplitSize, centeredSignalSize).toList()
            )
            calculateSplitCombinedResult()
        }

    private fun clearGlobalData() {
        mean1List.clear()
        mean2List.clear()
        mean3List.clear()
        timeList.clear()
        centeredSignal.clear()
    }
}
