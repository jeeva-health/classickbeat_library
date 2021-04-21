package ai.heart.classickbeats.ui.login.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentPhoneSignUpBinding
import ai.heart.classickbeats.utils.StringUtils
import ai.heart.classickbeats.utils.showShortToast
import ai.heart.classickbeats.utils.viewBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment


class PhoneSignUpFragment : Fragment(R.layout.fragment_phone_sign_up) {

    private val binding by viewBinding(FragmentPhoneSignUpBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        StringUtils.setTextViewHTML(
            binding.tnc,
            "By continuing you agree to our <a href=\"\">Terms & Conditions</a>"
        ) {
            showShortToast("No terms and No conditions")
        }
    }
}