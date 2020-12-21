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
        val output = processImage(image)
        image.close()
    }

    private fun processImage(image: ImageProxy) {
        val argbArray = yuv420ToARGB(image, context)
        // Process the output array here
        var h = image.height;
        var w = image.width;
        var offset: Float = 0F
        // Timber.i("Height: $h$, Width: $w$")
        displayCounter()
        // val arr_size = argbArray.count()
//        val a_sum = argbArray.sliceArray(0..(h*w-1))
//        val a_mean = a_sum.average() + offset
//        val r_sum = argbArray.sliceArray((h*w)..(2*h*w-1))
//        val r_mean = r_sum.average() + offset
//        val g_sum = argbArray.sliceArray((2*h*w)..(3*h*w-1))
//        val g_mean = g_sum.average() + offset
//        val b_sum = argbArray.sliceArray((3*h*w)..(4*h*w-1))
//        val b_mean = b_sum.average() + offset
//        //val n120 = argbArray.get(0)
//        Timber.i("A mean: $a_mean, R mean: $r_mean, G mean: $g_mean, B mean: $b_mean")
        // Timber.i("120th value: $n120$")

        val bitmap = yuvtoARGBvip(image, context)
        val colour = bitmap.getPixel(10, 10)

        val red: Int = Color.red(colour)
        val blue: Int = Color.blue(colour)
        val green: Int = Color.green(colour)
        val alpha: Int = Color.alpha(colour)
        Timber.i("A: $alpha, R: $red, G: $green, B: $blue")
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