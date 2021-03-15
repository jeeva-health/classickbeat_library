package ai.heart.classickbeats.ui.login

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentLoginBinding
import ai.heart.classickbeats.domain.TestType
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.showLongToast
import ai.heart.classickbeats.utils.showShortToast
import ai.heart.classickbeats.utils.viewBinding
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

const val RC_SIGN_IN = 1005

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val binding by viewBinding(FragmentLoginBinding::bind)

    private lateinit var loginButton: AppCompatButton

    private lateinit var signUpButton: AppCompatButton

    private lateinit var navController: NavController

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

        FirebaseApp.initializeApp(requireContext())

        binding.apply {
            loginButton = loginBtn
            signUpButton = signUpBtn
        }

        arrayOf(loginButton, signUpButton).forEach { button ->
            button.setSafeOnClickListener {
                //launchSignInFlow()
                navigateToScanFragment()
            }
        }

        checkPermission(Manifest.permission.CAMERA)
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        requestForPermissions()
    }

    private fun launchSignInFlow() {
        // Give users the option to sign in / register with their email
        // If users choose to register with their email,
        // they will need to create a password as well
        val providers = arrayListOf(
            AuthUI.IdpConfig.PhoneBuilder().setDefaultCountryIso("IN")
                .setWhitelistedCountries(listOf("IN")).build()
        )

        // Create and launch sign-in intent.
        // We listen to the response of this activity with the
        // SIGN_IN_RESULT_CODE code
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
                providers
            ).build(), RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                showShortToast("Login successful")
                navigateToScanFragment()
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                showLongToast("Login failed")
            }
        }
    }

    private fun navigateToScanFragment() {
        val action =
            LoginFragmentDirections.actionLoginFragmentToScanFragment(testType = TestType.HEART_RATE)
        navController.navigate(action)
    }
}