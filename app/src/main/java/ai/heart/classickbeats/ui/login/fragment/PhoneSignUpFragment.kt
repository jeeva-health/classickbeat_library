package ai.heart.classickbeats.ui.login.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentPhoneSignUpBinding
import ai.heart.classickbeats.shared.result.Event
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.ui.login.LoginViewModel
import ai.heart.classickbeats.utils.*
import android.os.Bundle
import android.os.CountDownTimer
import android.text.format.DateUtils
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import timber.log.Timber
import java.util.concurrent.TimeUnit

const val OTP_RETRY_DURATION = 40L
const val OTP_VALID_DURATION = 120L

class PhoneSignUpFragment : Fragment(R.layout.fragment_phone_sign_up) {

    private val binding by viewBinding(FragmentPhoneSignUpBinding::bind)

    private val logInViewModel by activityViewModels<LoginViewModel>()

    private var timer: CountDownTimer? = null

    var isTimerRunning: Boolean = false
        private set

    val timerProgress = MutableLiveData(Event(OTP_RETRY_DURATION))

    private lateinit var auth: FirebaseAuth
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                endTimer()
                Timber.d("onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Timber.w("onVerificationFailed: $e")
                endTimer()
                hideLoadingBar()
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
                updateViewForCodeSent()
                storedVerificationId = verificationId
                resendToken = token
                startTimer()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLightStatusBar()

        StringUtils.setTextViewHTML(
            binding.tnc,
            "By continuing you agree to our <a href=\"\">Terms & Conditions</a>"
        ) {
            showShortToast("No terms and No conditions")
        }

        binding.sendOtpBtn.setSafeOnClickListener {
            val phoneNumber = "+91" + binding.numberLayout.editText?.text?.toString()!!
            startPhoneNumberVerification(phoneNumber)
            showLoadingBar()
        }

        binding.verifyOtpBtn.setSafeOnClickListener {
            val otp = binding.otpLayout.editText?.text?.toString()!!
            verifyPhoneNumberWithCode(storedVerificationId, otp)
            showLoadingBar()
        }

        binding.resend.setSafeOnClickListener {
            val phoneNumber = "+91" + binding.numberLayout.editText?.text?.toString()!!
            resendVerificationCode(phoneNumber, resendToken)
            showLoadingBar()
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

            showLoading.observe(viewLifecycleOwner, {
                if (it) {
                    showLoadingBar()
                } else {
                    hideLoadingBar()
                }
            })
        }

        timerProgress.observe(viewLifecycleOwner, EventObserver {
            if (it <= 30) {
                binding.resendTxt.isVisible = true
                binding.resendTxt.text = "Didn't receive OTP? Retry in $it seconds."
            }
            if (it < 1) {
                updateViewForRetryTimeout()
            }
        })
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(OTP_VALID_DURATION, TimeUnit.SECONDS)
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
            .setTimeout(OTP_VALID_DURATION, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
        if (token != null) {
            optionsBuilder.setForceResendingToken(token)
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
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
                    hideLoadingBar()
                }
            }.addOnCanceledListener {
                hideLoadingBar()
            }
    }

    private fun updateViewForCodeSent() {
        binding.sendOtpBtn.visibility = View.GONE
        binding.verifyOtpBtn.visibility = View.VISIBLE
        binding.otpLayout.visibility = View.VISIBLE
        binding.numberLayout.isEnabled = false
        binding.otpLayout.requestFocus()
        hideLoadingBar()
    }

    private fun updateViewForRetryTimeout() {
        binding.resend.isVisible = true
        binding.resendTxt.isVisible = false
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

    fun startTimer(timeLeftInMillis: Long = OTP_RETRY_DURATION * DateUtils.SECOND_IN_MILLIS) {
        timer?.cancel()
        timer = object : CountDownTimer(timeLeftInMillis, TimeUnit.SECONDS.toMillis(1)) {
            override fun onFinish() {
                isTimerRunning = false
                timerProgress.postValue(Event(0))
            }

            override fun onTick(millisUntilFinished: Long) {
                timerProgress.postValue(Event((millisUntilFinished / DateUtils.SECOND_IN_MILLIS)))
            }
        }
        timer?.start()
        isTimerRunning = true
    }

    fun endTimer() {
        timer?.cancel()
        timer = null
    }
}
