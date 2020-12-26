package ai.heart.classickbeats.monitor

import android.content.Context
import android.graphics.Bitmap
import android.media.Image
import android.os.SystemClock
import android.renderscript.*
import android.text.format.DateUtils
import timber.log.Timber
import java.nio.ByteBuffer


class PixelAnalyzer constructor(
    private val context: Context,
    private val viewModel: MonitorViewModel
) {

    private var previousSecond: Long = 0
    private var currentSecond: Long = 0
    private var maxIndex: Int = 0
    private var counter: Int = 0
    private var imgCount: Int = 0
    private val skipFrames: Int = 60
    private val avgFrames: Int = 90

    private var script: ScriptIntrinsicYuvToRGB? = null
    private var inputAllocation: Allocation? = null
    private var outputAllocation: Allocation? = null
    private var initialised: Boolean = false
    private var sec = 0
    private var frameRate = 0
    private var firstSec = true

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        val data = ByteArray(remaining())
        get(data)
        return data
    }

    fun processImageSpO2(image: Image) {
        val argbArray = yuv420ToARGB(image, context)
        if (imgCount == (skipFrames + avgFrames)){
            maxIndex /= avgFrames
            Timber.i("Averaged MaxIndex is: $maxIndex")
        }
        if (imgCount >= (skipFrames + avgFrames)) {
            val h = image.height
            val w = image.width
            val size = argbArray.size
            var counter = 0
            var rSum = 0
            var gSum = 0
            var bSum = 0
            val len = 320
            val maxX = maxIndex % w
            val maxY = maxIndex / w
            var pixelSum = 0
            while (counter < size) {
                val counterX = (counter / 4) % w
                val counterY = (counter / 4) / w
                if (kotlin.math.abs(counterX - maxX) <= len && kotlin.math.abs(counterY - maxY) <= len) {
                    val byte = argbArray[counter].toInt()
                    when (counter % 4) {
                        0 -> rSum += byte and 0xFF
                        1 -> gSum += byte and 0xFF
                        2 -> bSum += byte and 0xFF
                    }
                    pixelSum++
                }
                counter++
            }
            val pixelCount = pixelSum / 4
            val rMax = argbArray[maxIndex * 4].toInt() and 0xFF
            val gMax = argbArray[maxIndex * 4 + 1].toInt() and 0xFF
            val bMax = argbArray[maxIndex * 4 + 2].toInt() and 0xFF
            Timber.i("${rSum.toDouble() / pixelCount} \t ${gSum.toDouble() / pixelCount} \t ${bSum.toDouble() / pixelCount}")
            Timber.i("MaxIndex: $maxIndex, Pixel count: $pixelCount, Image Count: $imgCount")
            Timber.i("rMax: $rMax, gMax: $gMax, bMax: $bMax")
            displayCounter()
        }
        imgCount++
    }

    fun processImageHeart(image: Image) {
        if (imgCount >= skipFrames) {
            val argbArray = yuv420ToARGB(image, context)
            val size = argbArray.size
            var counter = 0
            var rSum = 0
            var gSum = 0
            var bSum = 0
            while (counter < size) {
                val byte = argbArray[counter].toInt()
                when (counter % 4) {
                    0 -> rSum += byte and 0xFF
                    1 -> gSum += byte and 0xFF
                    2 -> bSum += byte and 0xFF
                }
                counter++
            }
            val pixelCount = size / 4
            Timber.i("${rSum.toDouble() / pixelCount} \t ${gSum.toDouble() / pixelCount} \t ${bSum.toDouble() / pixelCount}")
            displayCounter()
        }
        imgCount++
    }

    private fun displayCounter() {
        val currentTime = SystemClock.elapsedRealtime()
        previousSecond = currentSecond
        currentSecond = currentTime / DateUtils.SECOND_IN_MILLIS
        if (previousSecond == currentSecond) {
            counter++
        } else {
            if (firstSec){
                firstSec = false
            }
            else{
                sec++
                frameRate += ++counter
            }
            Timber.i("frameRate $previousSecond: ${counter} Seconds: $sec SumRate: $frameRate")
            counter = 0
        }
    }

    private fun yuv420ToARGB(image: Image, context: Context): ByteArray {
        val yuvByteArray = yuv420ToByteArray(image)

        if (!initialised) {
            val rs = RenderScript.create(context)
            script = ScriptIntrinsicYuvToRGB.create(rs, Element.U8(rs))

            val yuvType: Type.Builder = Type.Builder(rs, Element.U8(rs)).setX(yuvByteArray.size)
            inputAllocation = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT)
            val rgbaType: Type.Builder = Type.Builder(rs, Element.RGBA_8888(rs))
                .setX(image.width)
                .setY(image.height)
            outputAllocation = Allocation.createTyped(
                rs, rgbaType.create(), Allocation.USAGE_SCRIPT
            )
            initialised = true
        }

        inputAllocation?.copyFrom(yuvByteArray)
        script?.setInput(inputAllocation)
        script?.forEach(outputAllocation)

        val sizeOfImage = outputAllocation?.bytesSize ?: 0
        val outputArray = ByteArray(sizeOfImage)

//        val bitmap = Bitmap.createBitmap(
//            image.width, image.height, Bitmap.Config.ARGB_8888
//        )
//        outputAllocation?.copyTo(bitmap)

        outputAllocation?.copyTo(outputArray)
        return outputArray
    }

    private fun yuv420ToByteArray(image: Image): ByteArray {
        val yBuffer = image.planes[0].buffer
        val yData = yBuffer.toByteArray()
        if ((imgCount >= skipFrames) and (imgCount < (skipFrames + avgFrames))){
            val m = yData.indices.maxByOrNull { yData[it].toInt() and 0xFF } ?: -1
            Timber.i("MaxIndex: $m")
            maxIndex += m
        }
        val uBuffer = image.planes[1].buffer
        val uData = uBuffer.toByteArray()
        val vBuffer = image.planes[2].buffer
        val vData = vBuffer.toByteArray()
        return yData + vData + uData
    }
}