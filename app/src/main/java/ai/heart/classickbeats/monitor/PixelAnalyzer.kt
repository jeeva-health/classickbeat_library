package ai.heart.classickbeats.monitor

import android.R.attr.x
import android.R.attr.y
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.SystemClock
import android.renderscript.*
import android.text.format.DateUtils
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import timber.log.Timber
import java.nio.ByteBuffer
import kotlin.experimental.inv


class PixelAnalyzer constructor(private val context: Context) : ImageAnalysis.Analyzer {

    private var previousSecond: Long = 0
    private var currentSecond: Long = 0
    private var counter: Int = 0

    private var script: ScriptIntrinsicYuvToRGB? = null
    private var inputAllocation: Allocation? = null
    private var outputAllocation: Allocation? = null
    private var initialised: Boolean = false

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        val data = ByteArray(remaining())
        get(data)
        return data
    }

    override fun analyze(image: ImageProxy) {
        // Timber.i("cameraOutput: new Image received")
        // processImage(image)
        processImageVip(image)
        image.close()
    }

    private fun processImage(image: ImageProxy) {
        val argbArray = yuv420ToARGB(image, context)
        // Process the output array here
        val h = image.height
        val w = image.width
        val offset = 256.0
        var i: Int = 0
        var Rsum: ULong = 0u
        var Gsum: ULong = 0u
        var Bsum: ULong = 0u
        var Asum: ULong = 0u
        // Timber.i("Height: $h$, Width: $w$")
        displayCounter()
        val size = argbArray.count()
        while (i < size){
            Rsum += argbArray[i].toUInt()
            Gsum += argbArray[i+1].toUInt()
            Bsum += argbArray[i+2].toUInt()
            Asum += argbArray[i+3].toUInt()
            i += 4
        }

        val Rmean = Rsum.toFloat()/(size/4)
        val Gmean = Gsum.toFloat()/(size/4)
        val Bmean = Bsum.toFloat()/(size/4)
        val Amean = Asum.toFloat()/(size/4)
        val a = argbArray[160].toInt()
        val b = argbArray[161].toInt()
        Timber.i("$Rmean \t $Gmean \t $Bmean \t $Amean")
        Timber.i("Values: $a \t $b")
        Timber.i("Values: ${argbArray[0].toInt()} \t ${argbArray[1].toInt()} \t ${argbArray[2].toInt()} \t ${argbArray[3].toInt()}")
    }

    private fun processImageVip(image: ImageProxy) {
        displayCounter()
        val bitmap = yuvtoARGBvip(image, context)
        val h = image.height
        val w = image.width
        var colour = bitmap.getPixel(0, 0)
        var Rsum: Long = 0
        var Gsum: Long = 0
        var Bsum: Long = 0
        var Asum: Long = 0
        for (hh in 0..(h-1)) {
            for (ww in 0..(w-1)){
                colour = bitmap.getPixel(ww, hh)
                Rsum += Color.red(colour)
                Gsum += Color.green(colour)
                Bsum += Color.blue(colour)
                Asum += Color.alpha(colour)
            }
        }
        val Rmean = Rsum.toDouble()/(h*w)
        val Gmean = Gsum.toDouble()/(h*w)
        val Bmean = Bsum.toDouble()/(h*w)
        val Amean = Asum.toDouble()/(h*w)
        Timber.i("$Rmean \t $Gmean \t $Bmean \t $Amean")
    }

    private fun displayCounter() {
        val currentTime = SystemClock.elapsedRealtime()
        previousSecond = currentSecond
        currentSecond = currentTime / DateUtils.SECOND_IN_MILLIS
        // Timber.i("$previousSecond, $currentSecond")
        if (previousSecond == currentSecond) {
            counter++
        } else {
            Timber.i("Counter for $previousSecond: ${++counter}")
            counter = 0
        }
    }

    private fun yuv420ToARGB(image: ImageProxy, context: Context): ByteArray {
        val yuvByteArray = yuv420ToByteArray(image)

        if (!initialised) {
            val rs = RenderScript.create(context)
            script = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs))

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

        outputAllocation?.copyTo(outputArray)
        return outputArray
    }

    private fun yuvtoARGBvip(image: ImageProxy, context: Context): Bitmap {
        val byteArray = yuv420ToByteArray(image)
        val rs = RenderScript.create(context)
        val yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs))

        val yuvType = Type.Builder(rs, Element.U8(rs)).setX(byteArray.size)
        val inData = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT)

        val rgbaType = Type.Builder(rs, Element.RGBA_8888(rs)).setX(image.width).setY(image.height)
        val outData = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT)

        inData.copyFrom(byteArray)

        yuvToRgbIntrinsic.setInput(inData)
        yuvToRgbIntrinsic.forEach(outData)

        val bitmap = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
        outData.copyTo(bitmap)
        return bitmap
    }

    private fun yuv420ToByteArray(image: ImageProxy): ByteArray {
        val yBuffer = image.planes[0].buffer
        val yData = yBuffer.toByteArray()
        val uBuffer = image.planes[1].buffer
        val uData = uBuffer.toByteArray()
        val vBuffer = image.planes[2].buffer
        val vData = vBuffer.toByteArray()
        return yData + vData + uData
    }
}