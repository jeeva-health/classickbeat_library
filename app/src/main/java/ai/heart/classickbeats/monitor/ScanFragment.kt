package ai.heart.classickbeats.monitor

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentScanBinding
import ai.heart.classickbeats.utils.viewBinding
import ai.heart.classickbeats.widgets.CircleProgressBar
import android.Manifest
import android.content.Context.CAMERA_SERVICE
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.view.Surface
import android.view.TextureView
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class ScanFragment : Fragment(R.layout.fragment_scan) {

    private val binding by viewBinding(FragmentScanBinding::bind)

    private val monitorViewModel: MonitorViewModel by activityViewModels()

    private lateinit var backCamera: Camera
    private lateinit var frontCamera: Camera

    private var pixelAnalyzer: PixelAnalyzer? = null

    private var width: Int = 0
    private var height: Int = 0

    private var showBackCamera: Boolean = false

    private lateinit var textureView: TextureView
    private lateinit var cameraCaptureButton: AppCompatImageButton
    private lateinit var switchCamera: AppCompatImageView
    private lateinit var circularProgressBar: CircleProgressBar

    private val requiredPermissions = listOf(Manifest.permission.CAMERA)
    private val permissionToRequest = mutableListOf<String>()
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionResult ->

            requiredPermissions.forEach { permission ->
                if (permissionResult[permission] == true) {
                    permissionToRequest.remove(permission)
                }
            }
            if (ifAllMustPermissionsAreGranted()) {
                openCamera()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pixelAnalyzer = PixelAnalyzer(requireContext(), monitorViewModel)
        val cameraManager = requireActivity().getSystemService(CAMERA_SERVICE) as CameraManager
        backCamera = Camera(cameraManager, pixelAnalyzer!!, CameraCharacteristics.LENS_FACING_BACK)
        frontCamera =
            Camera(cameraManager, pixelAnalyzer!!, CameraCharacteristics.LENS_FACING_FRONT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkPermission(Manifest.permission.CAMERA)
        requestForPermissions()

        textureView = binding.viewFinder
        cameraCaptureButton = binding.cameraCaptureButton
        switchCamera = binding.switchCamera
        circularProgressBar = binding.circularProgressBar

        textureView.surfaceTextureListener = surfaceTextureListener

        cameraCaptureButton.setOnLongClickListener {
            it.visibility = View.GONE
            circularProgressBar.visibility = View.VISIBLE
            monitorViewModel.startTimer()
            startScanning()
            true
        }

        switchCamera.setOnClickListener {
            switchCamera()
        }

        monitorViewModel.timerProgress.observe(viewLifecycleOwner, {
            if (it == 0) {
                circularProgressBar.visibility = View.GONE
                cameraCaptureButton.visibility = View.VISIBLE
                endScanning()
            } else {
                val progress = ((SCAN_DURATION - it) * 100 / SCAN_DURATION).toFloat()
                circularProgressBar.setProgress(progress)
                circularProgressBar.invalidate()
            }
        })
    }

    private fun ifAllMustPermissionsAreGranted() = permissionToRequest.isEmpty()

    private fun requestForPermissions() {
        if (ifAllMustPermissionsAreGranted()) {
            openCamera()
        } else {
            requestPermissionLauncher.launch(permissionToRequest.toTypedArray())
        }
    }

    private fun checkPermission(permission: String) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionToRequest.add(permission)
        }
    }

    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(texture: SurfaceTexture, width: Int, height: Int) {
            this@ScanFragment.width = width
            this@ScanFragment.height = height
            openCamera()
        }

        override fun onSurfaceTextureSizeChanged(
            texture: SurfaceTexture,
            width: Int,
            height: Int
        ) {
        }

        override fun onSurfaceTextureDestroyed(texture: SurfaceTexture) = true
        override fun onSurfaceTextureUpdated(texture: SurfaceTexture) = Unit
    }

    private fun openCamera() {
        try {
            val selectedCamera = if (showBackCamera) {
                backCamera
            } else {
                frontCamera
            }

            selectedCamera.let {
                it.open()
                val texture = textureView.surfaceTexture
                texture?.setDefaultBufferSize(width, height)
                it.start(Surface(texture!!), monitorViewModel)
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun switchCamera() {
        val currentCamera = if (showBackCamera) {
            backCamera
        } else {
            frontCamera
        }
        currentCamera.close()
        showBackCamera != showBackCamera
        openCamera()
    }

    private fun startScanning() {
        monitorViewModel.isProcessing = true
        switchCamera.visibility = View.GONE
    }

    private fun endScanning() {
        monitorViewModel.isProcessing = false
        switchCamera.visibility = View.VISIBLE
    }
}