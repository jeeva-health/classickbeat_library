package ai.heart.classickbeats.monitor

import android.content.Context
import android.os.SystemClock
import android.renderscript.*
import android.text.format.DateUtils
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import timber.log.Timber
import java.nio.ByteBuffer


class PixelAnalyzer constructor(
    private val context: Context,
    private val viewModel: MonitorViewModel
) :
    ImageAnalysis.Analyzer {

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
        if (viewModel.isProcessing) {
            processImage(image)
        }
        image.close()
    }

    private fun processImage(image: ImageProxy) {
        val argbArray = yuv420ToARGB(image, context)
        val size = argbArray.size
        val iterator = argbArray.iterator()
        var counter = 0
        var rSum = 0
        var gSum = 0
        var bSum = 0
        while (iterator.hasNext()) {
            val byte = iterator.nextByte().toInt()
            val intVal = if (byte < 0) byte + 256 else byte
            if (++counter < 5) {
                continue
            }
            when (counter % 4) {
                1 -> rSum += intVal
                2 -> gSum += intVal
                3 -> bSum += intVal
            }
        }
        val rgbSize = size / 4 - 1
        Timber.i("rMean: ${rSum / rgbSize}, gMean: ${gSum / rgbSize} and bMean: ${bSum / rgbSize}")

        displayCounter()
    }

    private fun displayCounter() {
        val currentTime = SystemClock.elapsedRealtime()
        previousSecond = currentSecond
        currentSecond = currentTime / DateUtils.SECOND_IN_MILLIS
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

        outputAllocation?.copyTo(outputArray)
        return outputArray
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