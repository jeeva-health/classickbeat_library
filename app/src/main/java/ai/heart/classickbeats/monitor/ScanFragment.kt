package ai.heart.classickbeats.monitor

import ai.heart.classickbeats.MainActivity
import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentScanBinding
import ai.heart.classickbeats.utils.EventObserver
import ai.heart.classickbeats.utils.viewBinding
import ai.heart.classickbeats.widgets.CircleProgressBar
import android.animation.Animator
import android.content.Context.CAMERA_SERVICE
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.view.Surface
import android.view.TextureView
import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class ScanFragment : Fragment(R.layout.fragment_scan) {

    private val binding by viewBinding(FragmentScanBinding::bind)

    private val navArgs: ScanFragmentArgs by navArgs()

    private val monitorViewModel: MonitorViewModel by activityViewModels()

    private lateinit var navController: NavController

    private lateinit var backCamera: Camera
    private lateinit var frontCamera: Camera

    private var pixelAnalyzer: PixelAnalyzer? = null

    private var showBackCamera: Boolean = false

    private lateinit var textureView: TextureView
    private lateinit var cameraCaptureButton: AppCompatImageButton
    private lateinit var circularProgressBar: CircleProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pixelAnalyzer = PixelAnalyzer(requireContext(), monitorViewModel)
        val cameraManager = requireActivity().getSystemService(CAMERA_SERVICE) as CameraManager
        backCamera = Camera(cameraManager, pixelAnalyzer!!, CameraCharacteristics.LENS_FACING_BACK)
        frontCamera =
            Camera(cameraManager, pixelAnalyzer!!, CameraCharacteristics.LENS_FACING_FRONT)

        showBackCamera = when (navArgs.testType) {
            TestType.HEART_RATE -> true
            TestType.OXYGEN_SATURATION -> false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).hideSystemUI()

        navController = findNavController()

        textureView = binding.viewFinder
        cameraCaptureButton = binding.cameraCaptureButton
        circularProgressBar = binding.circularProgressBar

        textureView.surfaceTextureListener = surfaceTextureListener

        cameraCaptureButton.setOnLongClickListener {
            it.visibility = View.GONE
            binding.countdownAnimation.visibility = View.VISIBLE
            binding.countdownAnimation.playAnimation()
            binding.countdownAnimation.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator?) {
                }

                override fun onAnimationEnd(p0: Animator?) {
                    binding.countdownAnimation.visibility = View.GONE
                    circularProgressBar.visibility = View.VISIBLE
                    monitorViewModel.startTimer()
                    startScanning()
                }

                override fun onAnimationCancel(p0: Animator?) {
                }

                override fun onAnimationRepeat(p0: Animator?) {
                }

            })
            true
        }

        monitorViewModel.timerProgress.observe(viewLifecycleOwner, EventObserver {
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

    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(texture: SurfaceTexture, width: Int, height: Int) {
            openCamera(width, height)
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

    private fun openCamera(width: Int, height: Int) {
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

    override fun onDestroy() {
        super.onDestroy()

        try {
            backCamera.close()
            frontCamera.close()
            monitorViewModel.endTimer()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun startScanning() {
        monitorViewModel.isProcessing = true
    }

    private fun endScanning() {
        monitorViewModel.isProcessing = false
        when (navArgs.testType) {
            TestType.HEART_RATE -> navigateToHeartResultFragment()
            TestType.OXYGEN_SATURATION -> navigateToOxygenResultFragment()
        }
    }

    private fun navigateToHeartResultFragment() {
//        val action = ScanFragmentDirections.actionScanFragmentToHeartResultFragment()
//        navController.navigate(action)
    }

    private fun navigateToOxygenResultFragment() {
//        val action = ScanFragmentDirections.actionScanFragmentToOxygenResultFragment()
//        navController.navigate(action)
    }
}