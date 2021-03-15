package ai.heart.classickbeats.domain

import ai.heart.classickbeats.ui.monitor.MonitorViewModel
import ai.heart.classickbeats.ui.monitor.PixelAnalyzer
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Range
import android.view.Surface
import timber.log.Timber
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

class Camera constructor(
    private val cameraManager: CameraManager,
    private val pixelAnalyzer: PixelAnalyzer,
    private val lensFacing: Int
) {

    companion object {
        @Volatile
        var instance: Camera? = null
            private set

        fun initInstance(
            cameraManager: CameraManager,
            pixelAnalyzer: PixelAnalyzer,
            lensFacing: Int
        ): Camera {
            val i = instance
            if (i != null) {
                return i
            }
            return synchronized(this) {
                val created = Camera(cameraManager, pixelAnalyzer, lensFacing)
                instance = created
                created
            }
        }
    }

    private val characteristics: CameraCharacteristics

    val cameraId: String

    private val openLock = Semaphore(1)

    private var cameraDevice: CameraDevice? = null

    private var imageReader: ImageReader? = null

    private var captureSession: CameraCaptureSession? = null

    private var isClosed = true

    private var mBackgroundHandler: Handler? = null

    private var mBackgroundThread: HandlerThread? = null

    private var surface: Surface? = null

    private var viewModel: MonitorViewModel? = null

    init {
        cameraId = setUpCameraId(cameraManager, lensFacing)
        characteristics = cameraManager.getCameraCharacteristics(cameraId)
    }

    private val cameraStateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            openLock.release()
            isClosed = false
        }

        override fun onClosed(camera: CameraDevice) {
            isClosed = true
        }

        override fun onDisconnected(camera: CameraDevice) {
            openLock.release()
            camera.close()
            cameraDevice = null
            isClosed = true
        }

        override fun onError(camera: CameraDevice, error: Int) {
            openLock.release()
            camera.close()
            cameraDevice = null
            isClosed = true
        }
    }

    private val captureStateCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigureFailed(session: CameraCaptureSession) {
            //TODO: handle error
        }

        override fun onConfigured(session: CameraCaptureSession) {
            if (isClosed) return
            captureSession = session
            startPreview()
        }
    }

    fun open() {
        try {
            if (!openLock.tryAcquire(3L, TimeUnit.SECONDS)) {
                throw IllegalStateException("Camera launch failed")
            }
            if (cameraDevice != null) {
                openLock.release()
                return
            }
            startBackgroundHandler()
            cameraManager.openCamera(cameraId, cameraStateCallback, mBackgroundHandler)
        } catch (e: SecurityException) {
            Timber.e(e)
        }
    }

    fun start(surface: Surface, viewModel: MonitorViewModel) {

        this.surface = surface
        this.viewModel = viewModel

        imageReader = ImageReader.newInstance(640, 480, ImageFormat.YUV_420_888, 60)
        imageReader?.setOnImageAvailableListener(onImageAvailableListener, mBackgroundHandler)
        cameraDevice?.createCaptureSession(
            listOf(surface, imageReader?.surface),
            captureStateCallback,
            mBackgroundHandler
        )
    }

    private val onImageAvailableListener =
        ImageReader.OnImageAvailableListener { reader: ImageReader ->
            val img = reader.acquireLatestImage() ?: return@OnImageAvailableListener
            if (viewModel?.isProcessing == true) {
                if (lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                    pixelAnalyzer.processImage(img)
                } else {
                    pixelAnalyzer.processImage(img)
                }
            }
            img.close()
        }

    fun close() {
        try {
            if (openLock.tryAcquire(3, TimeUnit.SECONDS))
                isClosed = true
            captureSession?.close()
            captureSession = null

            cameraDevice?.close()
            cameraDevice = null

            surface?.release()
            surface = null

            imageReader?.close()
            imageReader = null
            stopBackgroundHandler()
        } catch (e: InterruptedException) {
            Timber.e("Error closing camera $e")
        } finally {
            openLock.release()
        }
    }

    private fun startPreview() {
        try {
            if (!openLock.tryAcquire(1L, TimeUnit.SECONDS)) return
            if (isClosed) return
            val builder = createPreviewRequestBuilder() ?: return
            captureSession?.setRepeatingRequest(
                builder.build(), null, mBackgroundHandler
            )
        } catch (e: Exception) {
            Timber.e(e)
        } finally {
            openLock.release()
        }
    }

    private fun createPreviewRequestBuilder(): CaptureRequest.Builder? {
        val builder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        enableDefaultModes(builder)
        builder?.addTarget(imageReader!!.surface)
        builder?.addTarget(surface!!)
        return builder
    }

    private fun enableDefaultModes(builder: CaptureRequest.Builder?) {
        if (builder == null) return

        builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_OFF)
        builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON)
//        builder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_OFF)
//        builder.set(
//            CaptureRequest.COLOR_CORRECTION_MODE,
//            CaptureRequest.COLOR_CORRECTION_ABERRATION_MODE_OFF
//        )

        val fpsRange: Range<Int> = Range(30, 30)
        builder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, fpsRange)

        if (lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
            builder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH)
        }
    }

    private fun setUpCameraId(manager: CameraManager, lensFacing: Int): String {
        for (cameraId in manager.cameraIdList) {
            val characteristics = manager.getCameraCharacteristics(cameraId)
            val cameraDirection = characteristics.get(CameraCharacteristics.LENS_FACING)
            characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES)
                ?.forEach { range ->
                    Timber.i("Supported FPS range: (${range.lower} - ${range.upper})")
                }
            if (cameraDirection != null &&
                cameraDirection == lensFacing
            ) {
                return cameraId
            }
        }
        throw IllegalStateException("Could not set Camera Id")
    }

    private fun startBackgroundHandler() {
        if (mBackgroundThread != null) return

        mBackgroundThread = HandlerThread("Camera-$cameraId").also {
            it.start()
            mBackgroundHandler = Handler(it.looper)
        }
    }

    private fun stopBackgroundHandler() {
        mBackgroundThread?.quitSafely()
        try {
            // TODO: investigate why thread does not end when join is called
            mBackgroundThread?.join()
            mBackgroundThread = null
            mBackgroundHandler = null
        } catch (e: InterruptedException) {
            Timber.e("===== stop background error $e")
        }
    }
}