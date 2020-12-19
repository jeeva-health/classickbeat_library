package ai.heart.classickbeats.monitor

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.*
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import timber.log.Timber
import java.nio.ByteBuffer
import kotlin.math.max
import kotlin.math.min


class PixelAnalyzer : ImageAnalysis.Analyzer {

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        val data = ByteArray(remaining())
        get(data)
        return data
    }

    override fun analyze(image: ImageProxy) {
        Timber.i("cameraOutput: new Image received")
        val output = processImage(image)
        Timber.i("cameraOutput: ${output[0]}")
        image.close()
    }

    private fun processImage(image: ImageProxy): FloatArray {

        val imageWidth = image.width
        val imageHeight = image.height
        // sRGB array needed by Bitmap static factory method I use below.
        val argbArray = IntArray(imageWidth * imageHeight)
        val yBuffer = image.planes[0].buffer
        yBuffer.position(0)

        // This is specific to YUV420SP format where U & V planes are interleaved
        // so you can access them directly from one ByteBuffer. The data is saved as
        // UVUVUVUVU... for NV12 format and VUVUVUVUV... for NV21 format.
        //
        // The alternative way to handle this would be refer U & V as separate
        // `ByteBuffer`s and then use PixelStride and RowStride to find the right
        // index of the U or V value per pixel.
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
        for (y in 0 until imageHeight - 2) {
            for (x in 0 until imageWidth - 2) {
                val yIndex = y * imageWidth + x
                // Y plane should have positive values belonging to [0...255]
                yValue = yBuffer[yIndex].toInt() and 0xff
                val uvx = x / 2
                val uvy = y / 2
                // Remember UV values are common for four pixel values.
                // So the actual formula if U & V were in separate plane would be:
                // `pos (for u or v) = (y / 2) * (width / 2) + (x / 2)`
                // But since they are in single plane interleaved the position becomes:
                // `u = 2 * pos`
                // `v = 2 * pos + 1`, if the image is in NV12 format, else reverse.
                val uIndex = uvy * imageWidth + 2 * uvx
                // ^ Note that here `uvy = y / 2` and `uvx = x / 2`
                val vIndex = uIndex + 1
                uValue = (uvBuffer[uIndex].toInt() and 0xff) - 128
                vValue = (uvBuffer[vIndex].toInt() and 0xff) - 128
                r = (yValue + 1.370705f * vValue).toInt()
                g = (yValue - 0.698001f * vValue - 0.337633f * uValue).toInt()
                b = (yValue + 1.732446f * uValue).toInt()
                r = clamp(r.toFloat(), 0f, 255f)
                g = clamp(g.toFloat(), 0f, 255f)
                b = clamp(b.toFloat(), 0f, 255f)
                // Use 255 for alpha value, no transparency. ARGB values are
                // positioned in each byte of a single 4 byte integer
                // [AAAAAAAARRRRRRRRGGGGGGGGBBBBBBBB]
                argbArray[yIndex] =
                    255 shl 24 or (r and 255 shl 16) or (g and 255 shl 8) or (b and 255)
                r_sum += r
                g_sum += g
                b_sum += b
                count++
                // float pixel_mean = (r + g + b)/3;
                // System.out.print(pixel_mean + " ");
            }
            // System.out.println("");
        }
        //        Bitmap bitmap = Bitmap.createBitmap(argbArray, imageWidth, imageHeight, Config.ARGB_8888);
//        printRGB(bitmap);
        val r_mean = r_sum.toFloat() / count.toFloat()
        val g_mean = g_sum.toFloat() / count.toFloat()
        val b_mean = b_sum.toFloat() / count.toFloat()

//        String r_mean_s = "Mean: " + r_sum + "     Count: " + count;
//        Log.d("",r_mean_s);
        val means = FloatArray(3)
        means[0] = r_mean
        means[1] = g_mean
        means[2] = b_mean
        return means
    }

    private fun clamp(value: Float, min: Float, max: Float): Int {
        return max(min, min(max, value)).toInt()
    }

//    fun yuv420ToBitmap(image: ImageProxy, context: Context): Bitmap? {
//        val rs = RenderScript.create(context)
//        val script = ScriptIntrinsicYuvToRGB.create(
//            rs, Element.U8_4(rs)
//        )
//
//        // Refer the logic in a section below on how to convert a YUV_420_888 image
//        // to single channel flat 1D array. For sake of this example I'll abstract it
//        // as a method.
//        val yuvByteArray: ByteArray = yuv420ToByteArray(image)
//        val yuvType: Type.Builder = Type.Builder(rs, Element.U8(rs))
//            .setX(yuvByteArray.size)
//        val `in` = Allocation.createTyped(
//            rs, yuvType.create(), Allocation.USAGE_SCRIPT
//        )
//        val rgbaType: Type.Builder = Type.Builder(rs, Element.RGBA_8888(rs))
//            .setX(image.getWidth())
//            .setY(image.getHeight())
//        val out = Allocation.createTyped(
//            rs, rgbaType.create(), Allocation.USAGE_SCRIPT
//        )
//
//        // The allocations above "should" be cached if you are going to perform
//        // repeated conversion of YUV_420_888 to Bitmap.
//        `in`.copyFrom(yuvByteArray)
//        script.setInput(`in`)
//        script.forEach(out)
//        val bitmap = Bitmap.createBitmap(
//            image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888
//        )
//        out.copyTo(bitmap)
//        return bitmap
//    }
}