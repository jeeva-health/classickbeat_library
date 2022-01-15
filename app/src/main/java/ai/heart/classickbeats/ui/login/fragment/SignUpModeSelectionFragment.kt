package ai.heart.classickbeats.ui.login.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentSignUpModeSelectionBinding
import ai.heart.classickbeats.utils.*
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController


class SignUpModeSelectionFragment : Fragment(R.layout.fragment_sign_up_mode_selection) {

    private val binding by viewBinding(FragmentSignUpModeSelectionBinding::bind)

    private lateinit var navController: NavController

    private lateinit var googleModeButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        googleModeButton = binding.googleSignIn

        setDarkStatusBar()

        binding.tnc.text = setClickableText(
            SpannableString.valueOf(getString(R.string.terms_and_condition)),
            32,
            50,
        )
        binding.tnc.setSafeOnClickListener {
            openTnCPage()
        }

        googleModeButton.setSafeOnClickListener {
            navigateGoogleSignUpFragment()
        }
    }

    private fun navigateToPhoneSignUpFragment() {
        val action =
            SignUpModeSelectionFragmentDirections.actionSignUpModeSelectionFragmentToPhoneSignUpFragment()
        navController.navigate(action)
    }

    private fun navigateGoogleSignUpFragment() {
        val action =
            SignUpModeSelectionFragmentDirections.actionSignUpModeSelectionFragmentToGoogleSignUpFragment()
        navController.navigate(action)
    }

    private fun setClickableText(
        message: SpannableString,
        startPos: Int,
        endPos: Int,
    ): SpannableString {
        message.setSpan(
            StyleSpan(Typeface.BOLD),
            startPos,
            endPos,
            SpannableString.SPAN_INCLUSIVE_INCLUSIVE
        )
        message.setSpan(
            ForegroundColorSpan(getContextColor(R.color.orange)),
            startPos,
            endPos,
            SpannableString.SPAN_INCLUSIVE_INCLUSIVE
        )
        return message
    }

    private fun openTnCPage() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.intealth.app/termsOfUse"))
        startActivity(intent)
    }
}
