package ai.heart.classickbeats.ui.login.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentLoginBinding
import ai.heart.classickbeats.storage.SharedPreferenceStorage
import ai.heart.classickbeats.ui.login.LoginViewModel
import ai.heart.classickbeats.utils.*
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
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

const val RC_SIGN_IN = 1005

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val binding by viewBinding(FragmentLoginBinding::bind)

    private val logInViewModel by activityViewModels<LoginViewModel>()

    @Inject
    lateinit var sharedPreferenceStorage: SharedPreferenceStorage

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
                observeAuthenticationState()
                launchSignInFlow()
            }
        }

        checkPermission(Manifest.permission.CAMERA)
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        requestForPermissions()
    }

    private fun launchSignInFlow() {
//        // Give users the option to sign in / register with their email
//        // If users choose to register with their email,
//        // they will need to create a password as well
//        val providers = arrayListOf(
//            AuthUI.IdpConfig.PhoneBuilder().setDefaultCountryIso("IN")
//                .setWhitelistedCountries(listOf("IN", "US")).build()
//        )
//
//        // Create and launch sign-in intent.
//        // We listen to the response of this activity with the
//        // SIGN_IN_RESULT_CODE code
//        startActivityForResult(
//            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
//                providers
//            ).build(), RC_SIGN_IN
//        )
    }

    private fun observeAuthenticationState() {

        logInViewModel.apply {

//            firebaseAuthenticationState.observe(
//                viewLifecycleOwner,
//                EventObserver { authState ->
//                    when (authState) {
//                        LoginViewModel.AuthenticationState.INVALID_AUTHENTICATION -> {
//                            Toast.makeText(activity, "Login Failed", Toast.LENGTH_LONG).show()
//                        }
//                        LoginViewModel.AuthenticationState.AUTHENTICATED -> {
//                            Toast.makeText(activity, "Login Successfully", Toast.LENGTH_LONG).show()
//                            val firebaseToken = currentFirebaseUser?.getIdToken(false)?.result?.token
//                            loginUser(firebaseToken)
//                        }
//                    }
//                })

//            loginState.observe(viewLifecycleOwner, EventObserver { isUserLoggedIn ->
//                if (isUserLoggedIn) {
//                    if (isUserRegistered) {
//                        navigateToSelectionFragment()
//                    } else {
//                        navigateToRegisterFragment()
//                    }
//                }
//            })

            showLoading.observe(
                viewLifecycleOwner, { showLoading ->
                    if (showLoading) {
                        showLoadingBar()
                    } else {
                        hideLoadingBar()
                    }
                })
        }
    }
}