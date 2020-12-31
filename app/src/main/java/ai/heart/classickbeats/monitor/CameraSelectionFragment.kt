package ai.heart.classickbeats.monitor

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentCameraSelectionBinding
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.viewBinding
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CameraSelectionFragment : Fragment(R.layout.fragment_camera_selection) {

    private val binding by viewBinding(FragmentCameraSelectionBinding::bind)

    private lateinit var navController: NavController

    private lateinit var oxygenTestButton: MaterialButton
    private lateinit var heartTestButton: MaterialButton

    private val requiredPermissions = listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val permissionToRequest = mutableListOf<String>()
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionResult ->

            requiredPermissions.forEach { permission ->
                if (permissionResult[permission] == true) {
                    permissionToRequest.remove(permission)
                }
            }
            if (ifAllMustPermissionsAreGranted()) {

            }
        }

    private fun ifAllMustPermissionsAreGranted() = permissionToRequest.isEmpty()

    private fun requestForPermissions() {
        if (ifAllMustPermissionsAreGranted()) {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        oxygenTestButton = binding.oxygenButton
        heartTestButton = binding.heartButton

        oxygenTestButton.setSafeOnClickListener {
            navigateToScanFragment(TestType.OXYGEN_SATURATION)
        }

        heartTestButton.setSafeOnClickListener {
            navigateToScanFragment(TestType.HEART_RATE)
        }

        checkPermission(Manifest.permission.CAMERA)
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        requestForPermissions()
        // Toast.makeText(requireContext(), getPythonHelloWorld(), Toast.LENGTH_LONG).show()
    }

    private fun navigateToScanFragment(testType: TestType) {
        val action =
            CameraSelectionFragmentDirections.actionCameraSelectionFragmentToScanFragmentBorrowed(
                testType
            )
        navController.navigate(action)
    }

//    private fun getPythonHelloWorld(): String {
//        val python = Python.getInstance()
//        val pythonFile = python.getModule("HelloWorld")
//        return pythonFile.callAttr("myfunc(5)").toString()
//    }
}
