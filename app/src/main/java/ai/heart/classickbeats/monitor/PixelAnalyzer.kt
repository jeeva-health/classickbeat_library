package ai.heart.classickbeats.monitor

import android.content.Context
import android.media.Image
import android.os.SystemClock
import android.renderscript.*
import android.text.format.DateUtils
import timber.log.Timber
import java.nio.ByteBuffer
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt


class PixelAnalyzer constructor(
    private val context: Context,
    private val viewModel: MonitorViewModel
) {

    private var previousSecond: Long = 0
    private var currentSecond: Long = 0
    private var maxIndex: Int = 0
    private var counter: Int = 0
    private var imgCount: Int = 0
    private val skipFrames: Int = 0
    private val avgFrames: Int = 90

    private var script: ScriptIntrinsicYuvToRGB? = null
    private var inputAllocation: Allocation? = null
    private var outputAllocation: Allocation? = null
    private var initialised: Boolean = false
    private var sec = 0
    private var frameRate = 0
    private var firstSec = true
    private var secondSec = true

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        val data = ByteArray(remaining())
        get(data)
        return data
    }

    fun processImageSpO2(image: Image) {
        val argbArray = yuv420ToARGB(image, context)
        if (imgCount == (avgFrames)) {
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

    fun processImageSpO2Y(image: Image): Pair<Double, Int> {
        val argbArray = yuv420ToARGB(image, context)
        var yMean = 0.0
        var fps = 0
        if (imgCount == (avgFrames)) {
            maxIndex /= avgFrames
            Timber.i("Averaged MaxIndex is: $maxIndex")
        }
        if (imgCount >= avgFrames) {
            val yBuffer = image.planes[0].buffer
            val w = image.width
            val h = image.height
            val maxX = maxIndex % w
            val maxY = maxIndex / w
            val yData = yBuffer.toByteArray()
            var ySum = 0
            val len = 20
            var count = 0
            var ind = 0
            for (y in max(0, maxY - len) until min(h - 2, maxY + len)) {
                for (x in max(0, maxX - len) until min(w - 2, maxX + len)) {
                    ind = y * w + x
                    ySum += yData[ind].toInt() and 0xFF
                    count++
                }
            }
            //        while (counter < size) {
            //            val byte = yData[counter].toInt()
            //            ySum += byte and 0xFF
            //            counter++
            //        }
            yMean = ySum.toDouble() / count
            displayCounter()
            val fps = if (sec > 0) {
                (frameRate.toDouble() / sec).roundToInt()
            } else {
                0
            }
            Timber.i("YMean: $yMean \t FPS: $fps \t $h \t $w")
        }
        imgCount++
        return Pair(yMean, fps)
    }

    fun processImageHeart(image: Image): Triple<Double, Double, Int> {
        val argbArray = yuv420ToARGB(image, context)
        val size = argbArray.size
        val w = image.width
        val h = image.height
        var ind = 0
        var rSum = 0
        var gSum = 0
        var bSum = 0
        var aSum = 0
        val len = 320
        var count = 0
        for (y in max(0,h/2 - len) until min(h - 2, h/2 + len)) {
            for (x in max(0, w/2 - len) until min(w - 2, w/2 + len)) {
                ind = y * w + x
                rSum += argbArray[ind*4].toInt() and 0xFF
                gSum += argbArray[ind*4+1].toInt() and 0xFF
                bSum += argbArray[ind*4+2].toInt() and 0xFF
                // aSum += argbArray[ind*4+3].toInt() and 0xFF
                count++
            }
        }
//        while (counter < size) {
//            val byte = argbArray[counter].toInt()
//            when (counter % 4) {
//                0 -> rSum += byte and 0xFF
//                1 -> gSum += byte and 0xFF
//                2 -> bSum += byte and 0xFF
//            }
//            counter++
//        }
        val rMean = rSum.toDouble() / count
        val gMean = gSum.toDouble() / count
        val bMean = bSum.toDouble() / count
        // val aMean = aSum.toDouble() / count
        // displayCounter()
        val timeStamp = SystemClock.elapsedRealtime().toInt()
        val fps = if (sec > 0){
            (frameRate.toDouble()/sec).roundToInt()
        }
        else{
            0
        }
        Timber.i("RGBMean: $rMean \t $gMean \t $bMean \t TimeStamp: $timeStamp \t FPS: $fps")
        return Triple(rMean, gMean, timeStamp)
    }

    fun processImage(image: Image): Triple<Double, Double, Int> {
        val w = image.width
        val h = image.height
        // val argbArray = IntArray(w * h)
        val yBuffer = image.planes[0].buffer
        yBuffer.position(0)
        val uvBuffer = image.planes[1].buffer
        uvBuffer.position(0)
        var r: Int
        var g: Int
        var b: Int
        var yValue: Int
        var uValue: Int
        var vValue: Int
        var r_sum = 0
        var g_sum = 0
        var b_sum = 0
        var count = 0
        val len = 320  // Length of square is (2*len)
        for (y in max(0,h/2 - len) until min(h - 2, h/2 + len)) {
            for (x in max(0, w/2 - len) until min(w - 2, w/2 + len)) {
                val yIndex = y * w + x
                yValue = yBuffer[yIndex].toInt() and 0xff
                val uvx = x / 2
                val uvy = y / 2
                val uIndex = uvy * w + 2 * uvx
                val vIndex = uIndex + 1
                uValue = (uvBuffer[uIndex].toInt() and 0xff) - 128
                vValue = (uvBuffer[vIndex].toInt() and 0xff) - 128
                r = (yValue + 1.370705f * vValue).toInt()
                g = (yValue - 0.698001f * vValue - 0.337633f * uValue).toInt()
                b = (yValue + 1.732446f * uValue).toInt()
                r = clamp(r.toFloat(), 0f, 255f).toInt()
                g = clamp(g.toFloat(), 0f, 255f).toInt()
                b = clamp(b.toFloat(), 0f, 255f).toInt()
//                argbArray[yIndex] = 255 shl 24 or (r and 255 shl 16) or (g and 255 shl 8) or (b and 255)
                r_sum += r
                g_sum += g
                b_sum += b
                count++
            }
        }
        val rMean = r_sum.toDouble() / count
        val gMean = g_sum.toDouble() / count
        val bMean = b_sum.toDouble() / count
        displayCounter()
        val fps = if (sec > 0){
            (frameRate.toDouble()/sec).roundToInt()
        }
        else{
            0
        }
        val timeStamp = SystemClock.elapsedRealtime().toInt()
        Timber.i("rgbMean: " + rMean + "\t" + gMean + "\t" + bMean + "\t" + fps)
        return Triple(rMean, bMean, timeStamp)
    }

    private fun clamp(value: Float, min: Float, max: Float): Float {
        return Math.max(min, Math.min(max, value))
    }

    private fun displayCounter() {
        val currentTime = SystemClock.elapsedRealtime()
        previousSecond = currentSecond
        currentSecond = currentTime / DateUtils.SECOND_IN_MILLIS
        if (previousSecond == currentSecond) {
            counter++
        } else {
            if (firstSec) {
                firstSec = false
            } else if (secondSec) {
                secondSec = false
            } else {
                sec++
                frameRate += ++counter
            }
            Timber.i("frameRate: ${counter} Seconds: $sec SumRate: $frameRate")
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
//        if (imgCount < avgFrames) {
//            val m = yData.indices.maxBy{ yData[it].toInt() and 0xFF } ?: -1
//            Timber.i("MaxIndex: $m")
//            maxIndex += m
//        }
        val uBuffer = image.planes[1].buffer
        val uData = uBuffer.toByteArray()
        val vBuffer = image.planes[2].buffer
        val vData = vBuffer.toByteArray()
        return yData + vData + uData
    }

//    private fun yuv420ToByteArray(image: Image): ByteArray {
//        val width = image.width
//        val height = image.height
//        val ySize = width * height
//        val uvSize = width * height / 4
//        val nv21 = ByteArray(ySize + uvSize * 2)
//        val yBuffer = image.planes[0].buffer // Y
//        val uBuffer = image.planes[1].buffer // U
//        val vBuffer = image.planes[2].buffer // V
//        var rowStride = image.planes[0].rowStride
//        assert(image.planes[0].pixelStride == 1)
//        var pos = 0
//        if (rowStride == width) { // likely
//            yBuffer[nv21, 0, ySize]
//            pos += ySize
//        } else {
//            var yBufferPos = -rowStride.toLong() // not an actual position
//            while (pos < ySize) {
//                yBufferPos += rowStride.toLong()
//                yBuffer.position(yBufferPos.toInt())
//                yBuffer[nv21, pos, width]
//                pos += width
//            }
//        }
//        rowStride = image.planes[2].rowStride
//        val pixelStride = image.planes[2].pixelStride
//        assert(rowStride == image.planes[1].rowStride)
//        assert(pixelStride == image.planes[1].pixelStride)
//        if (pixelStride == 2 && rowStride == width && uBuffer[0] == vBuffer[1]) {
//            // maybe V an U planes overlap as per NV21, which means vBuffer[1] is alias of uBuffer[0]
//            val savePixel = vBuffer[1]
//            try {
//                vBuffer.put(1, savePixel.inv() as Byte)
//                if (uBuffer[0] == savePixel.inv() as Byte) {
//                    vBuffer.put(1, savePixel)
//                    vBuffer.position(0)
//                    uBuffer.position(0)
//                    vBuffer[nv21, ySize, 1]
//                    uBuffer[nv21, ySize + 1, uBuffer.remaining()]
//                    return nv21 // shortcut
//                }
//            } catch (ex: ReadOnlyBufferException) {
//                // unfortunately, we cannot check if vBuffer and uBuffer overlap
//            }
//
//            // unfortunately, the check failed. We must save U and V pixel by pixel
//            vBuffer.put(1, savePixel)
//        }
//
//        // other optimizations could check if (pixelStride == 1) or (pixelStride == 2),
//        // but performance gain would be less significant
//        for (row in 0 until height / 2) {
//            for (col in 0 until width / 2) {
//                val vuPos = col * pixelStride + row * rowStride
//                nv21[pos++] = vBuffer[vuPos]
//                nv21[pos++] = uBuffer[vuPos]
//            }
//        }
//        return nv21
//    }
}