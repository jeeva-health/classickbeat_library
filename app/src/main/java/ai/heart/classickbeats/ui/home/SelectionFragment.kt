package ai.heart.classickbeats.ui.home

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentSelectionBinding
import ai.heart.classickbeats.domain.TestType
import ai.heart.classickbeats.ui.login.LoginViewModel
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.viewBinding
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SelectionFragment : Fragment(R.layout.fragment_selection) {

    private val binding by viewBinding(FragmentSelectionBinding::bind)

    private val logInViewModel by activityViewModels<LoginViewModel>()

    private lateinit var navController: NavController

    private lateinit var scanButton: AppCompatButton

    private lateinit var logButton: AppCompatButton

    private lateinit var logoutButton: AppCompatButton

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

        scanButton = binding.scan
        logButton = binding.log
        logoutButton = binding.logout

        scanButton.setSafeOnClickListener {
            navigateToScanFragment()
        }

        logButton.setSafeOnClickListener {
            navigateToLoggingFragment()
        }

        logoutButton.setSafeOnClickListener {
            logInViewModel.logoutUser()
            requireActivity().finish()
        }

        checkPermission(Manifest.permission.CAMERA)
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        requestForPermissions()
    }

    private fun navigateToScanFragment() {
        val action =
            SelectionFragmentDirections.actionSelectionFragmentToScanFragment(testType = TestType.HEART_RATE)
        navController.navigate(action)
    }

    private fun navigateToLoggingFragment() {
        val action = SelectionFragmentDirections.actionSelectionFragmentToLogBpFragment()
        navController.navigate(action)
    }
}