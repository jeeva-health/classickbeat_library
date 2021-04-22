package ai.heart.classickbeats.ui.login.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentPhoneSignUpBinding
import ai.heart.classickbeats.ui.login.LoginViewModel
import ai.heart.classickbeats.utils.*
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import timber.log.Timber
import java.util.concurrent.TimeUnit


class PhoneSignUpFragment : Fragment(R.layout.fragment_phone_sign_up) {

    private val binding by viewBinding(FragmentPhoneSignUpBinding::bind)

    private val logInViewModel by activityViewModels<LoginViewModel>()

    private lateinit var auth: FirebaseAuth
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Timber.d("onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Timber.w("onVerificationFailed: $e")
                showShortToast("Error occurred!!!")
                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Timber.d("onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        StringUtils.setTextViewHTML(
            binding.tnc,
            "By continuing you agree to our <a href=\"\">Terms & Conditions</a>"
        ) {
            showShortToast("No terms and No conditions")
        }

        binding.sendOtpBtn.setSafeOnClickListener {
            val phoneNumber = "+91" + binding.numberLayout.editText?.text?.toString()!!
            startPhoneNumberVerification(phoneNumber)
            it.visibility = View.GONE
            binding.verifyOtpBtn.visibility = View.VISIBLE
            binding.otpLayout.visibility = View.VISIBLE
        }

        binding.verifyOtpBtn.setSafeOnClickListener {
            val otp = binding.otpLayout.editText?.text?.toString()!!
            verifyPhoneNumberWithCode(storedVerificationId, otp)
        }

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
        }
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
        if (token != null) {
            optionsBuilder.setForceResendingToken(token)
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.d("signInWithCredential:success")
                    val user = task.result?.user
                    val firebaseToken = user?.getIdToken(false)?.result?.token
                    logInViewModel.loginUser(firebaseToken!!)
                } else {
                    Timber.w("signInWithCredential:failure ${task.exception}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        showLongToast("Incorrect OTP")
                    } else {
                        showLongToast("Error occurred!!!")
                    }
                }
            }
    }

    private fun navigateToRegisterFragment() {
        val action =
            PhoneSignUpFragmentDirections.actionPhoneSignUpFragmentToPersonalDetailsFragment()
        findNavController().navigate(action)
    }

    private fun navigateToHomeFragment() {
        val action = PhoneSignUpFragmentDirections.actionPhoneSignUpFragmentToNavHome()
        findNavController().navigate(action)
    }
}