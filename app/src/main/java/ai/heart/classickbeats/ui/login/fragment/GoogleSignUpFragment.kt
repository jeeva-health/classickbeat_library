package ai.heart.classickbeats.ui.login.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.ui.login.FirebaseGoogleSignIn
import ai.heart.classickbeats.ui.login.LoginViewModel
import ai.heart.classickbeats.utils.hideLoadingBar
import ai.heart.classickbeats.utils.showLoadingBar
import ai.heart.classickbeats.utils.showLongToast
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import timber.log.Timber


class GoogleSignUpFragment : Fragment(R.layout.fragment_google_sign_up) {

    private lateinit var auth: FirebaseAuth

    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var navController: NavController

    private val logInViewModel by activityViewModels<LoginViewModel>()

    private val googleSigInLauncher = registerForActivityResult(FirebaseGoogleSignIn()) {
        if (it != null) {
            Timber.d("firebaseAuthWithGoogle: ${it.id}")
            firebaseAuthWithGoogle(it.idToken!!)
        } else {
            Timber.e("Google sign in failed")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        googleSigInLauncher.launch(googleSignInClient)

        logInViewModel.apply {
            loginState.observe(viewLifecycleOwner, EventObserver { isUserLoggedIn ->
                if (isUserLoggedIn) {
                    if (isUserRegistered) {
                        navigateToHomeFragment()
                    } else {
                        navigateToRegisterFragment()
                    }
                }
            })

            showLoading.observe(viewLifecycleOwner) {
                if (it) {
                    showLoadingBar()
                } else {
                    hideLoadingBar()
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Timber.d("signInWithCredential:success")
                    val user = auth.currentUser
                    val firebaseToken = user?.getIdToken(false)?.result?.token
                    logInViewModel.loginUser(firebaseToken!!)
                } else {
                    Timber.e("signInWithCredential:failure ${task.exception}")
                    showLongToast("Google login failed")
                    navController.navigateUp()
                }
            }
    }

    private fun navigateToHomeFragment() {
        val action = GoogleSignUpFragmentDirections.actionGoogleSignUpFragmentToNavHome()
        navController.navigate(action)
    }

    private fun navigateToRegisterFragment() {
        val action =
            GoogleSignUpFragmentDirections.actionGoogleSignUpFragmentToPersonalDetailsFragment()
        navController.navigate(action)
    }
}