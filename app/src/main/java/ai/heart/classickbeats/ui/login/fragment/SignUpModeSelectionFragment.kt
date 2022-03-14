package ai.heart.classickbeats.ui.login.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentSignUpModeSelectionBinding
import ai.heart.classickbeats.utils.getContextColor
import ai.heart.classickbeats.utils.setDarkStatusBar
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.viewBinding
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController

class SignUpModeSelectionFragment : Fragment(R.layout.fragment_sign_up_mode_selection) {

    private val binding by viewBinding(FragmentSignUpModeSelectionBinding::bind)

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setDarkStatusBar()

        binding.tagLine.text =
            setColoredText(
                setColoredText(
                    SpannableString.valueOf(getString(R.string.app_tagline)),
                    0,
                    7,
                    getContextColor(R.color.rosy_pink)
                ), 8, 18, getContextColor(R.color.soft_blue_3)
            )

        binding.tnc.text = setClickableText(
            SpannableString.valueOf(getString(R.string.terms_and_condition)),
            32,
            50,
        )
        binding.tnc.setSafeOnClickListener {
            openTnCPage()
        }

        binding.connectWithGoogleCard.setSafeOnClickListener {
            navigateGoogleSignUpFragment()
        }
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

    private fun setColoredText(
        message: SpannableString,
        startPos: Int,
        endPos: Int,
        colorId: Int,
    ): SpannableString {
        message.setSpan(
            ForegroundColorSpan(colorId),
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
