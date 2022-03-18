package ai.heart.classickbeats.ui.login.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentPersonalDetailsBinding
import ai.heart.classickbeats.model.Gender
import ai.heart.classickbeats.model.User
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.ui.login.LoginViewModel
import ai.heart.classickbeats.utils.setLightStatusBar
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.showSnackbar
import ai.heart.classickbeats.utils.viewBinding
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PersonalDetailsFragment : Fragment(R.layout.fragment_personal_details) {

    private val binding by viewBinding(FragmentPersonalDetailsBinding::bind)

    private val logInViewModel by activityViewModels<LoginViewModel>()

    private lateinit var navController: NavController

    private lateinit var dialogWeight: Dialog
    private lateinit var dialogHeight: Dialog
    private lateinit var dialogWhyAsk: Dialog

    private var weight = 0
    private var height = 0
    private var heightUnit = "cm"



    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setLightStatusBar()

        logInViewModel.currentUser?.let {
            binding.fullNameLayout.editText?.setText(it.fullName)
        }

        weightDialog()

        heightDialog()

        binding.dobDay.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 2){
                    binding.dobMonth.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        binding.dobMonth.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 2){
                    binding.dobYear.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })


        binding.whyAsk.setOnClickListener {
            whyAskDialog()
        }

        binding.weightLayout.setOnClickListener {
            dialogWeight.show()
        }

        binding.heightLayout.setOnClickListener {
            dialogHeight.show()
        }

        binding.done.setSafeOnClickListener {
            val name = binding.fullNameLayout.editText?.text?.toString() ?: ""
            val dob =
                binding.dobYear.text?.toString() + "-" + binding.dobMonth.text?.toString() + "-" + binding.dobDay.text?.toString()
            val user = User(
                fullName = name,
                gender = logInViewModel.selectedGender.value ?: Gender.MALE,
                weight = weight.toDouble(),
                height = height.toDouble(),
                dob = dob
            )
            logInViewModel.registerUser(user)
        }

        logInViewModel.apiResponse.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                LoginViewModel.RequestType.LOGIN -> TODO()
                LoginViewModel.RequestType.REGISTER -> {
                    showSnackbar("Successfully Registered")
                    navigateToNavHome()
                }
            }
        })
    }

    private fun whyAskDialog() {
        // setting up Why Ask Dialog
        dialogWhyAsk = Dialog(requireContext())
        dialogWhyAsk.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogWhyAsk.setContentView(R.layout.dialog_why_ask)
        dialogWhyAsk.setCancelable(true)
        val gotIt: TextView = dialogWhyAsk.findViewById(R.id.got_it)
        dialogWhyAsk.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialogWhyAsk.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.bg_semi_rounded_rectangle_32
            )
        )
        dialogWhyAsk.window?.attributes?.windowAnimations =
            R.style.Animation_Design_BottomSheetDialog
        dialogWhyAsk.window?.setGravity(Gravity.BOTTOM)

        dialogWhyAsk.show()

        gotIt.setOnClickListener {
            dialogWhyAsk.dismiss()
        }
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    private fun weightDialog() {
        // setting up Weight Dialog
        dialogWeight = Dialog(requireContext())
        dialogWeight.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogWeight.setContentView(R.layout.dialog_weight)
        val wtIncrement: ImageView = dialogWeight.findViewById(R.id.increment_weight)
        val wtDecrement: ImageView = dialogWeight.findViewById(R.id.decrement_weight)
        val wtInput: EditText = dialogWeight.findViewById(R.id.input_weight)
        val wtDone: Button = dialogWeight.findViewById(R.id.got_it)

        dialogWeight.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialogWeight.setCancelable(false)
        dialogWeight.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.bg_semi_rounded_rectangle_32
            )
        )
        dialogWeight.window?.attributes?.windowAnimations =
            R.style.Animation_Design_BottomSheetDialog
        dialogWeight.window?.setGravity(Gravity.BOTTOM)

        wtInput.setText(weight.toString())

        wtIncrement.setOnClickListener {
            weight += 1
            wtInput.setText(weight.toString())
        }

        wtDecrement.setOnClickListener {

            if (weight > 0) {
                weight -= 1
                wtInput.setText(weight.toString())
            }
        }

        wtDone.setOnClickListener {
            weight = Integer.parseInt(wtInput.text.toString())
            binding.weightValue.text = "$weight KG"
            binding.weightValue.setTextColor(R.color.black)
            dialogWeight.dismiss()
        }


    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    private fun heightDialog() {
        // setting up Height Dialog
        dialogHeight = Dialog(requireContext())
        dialogHeight.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogHeight.setContentView(R.layout.dialog_height)
        val htIncrement: ImageView = dialogHeight.findViewById(R.id.increment_height)
        val htDecrement: ImageView = dialogHeight.findViewById(R.id.decrement_height)
        val htDone: Button = dialogHeight.findViewById(R.id.height_done)
        val toggleCm: TextView = dialogHeight.findViewById(R.id.toggle_cm)
        val toggleFeet: TextView = dialogHeight.findViewById(R.id.toggle_feet)
        val constrainCm: ConstraintLayout = dialogHeight.findViewById(R.id.constrain_cm)
        val constrainFeet: ConstraintLayout = dialogHeight.findViewById(R.id.constrain_feet)
        val htInputCm: EditText = dialogHeight.findViewById(R.id.input_cm)
        val htInputFeet: EditText = dialogHeight.findViewById(R.id.input_feet)
        val htInputInch: EditText = dialogHeight.findViewById(R.id.input_inch)

        dialogHeight.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialogHeight.setCancelable(false)
        dialogHeight.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.bg_semi_rounded_rectangle_32
            )
        )
        dialogHeight.window?.attributes?.windowAnimations =
            R.style.Animation_Design_BottomSheetDialog
        dialogHeight.window?.setGravity(Gravity.BOTTOM)

        toggleCm.setBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.bg_rectangle_cornered_4dp
            )
        )
        toggleCm.setTextColor(R.color.dark_red)
        constrainCm.visibility = View.VISIBLE


        toggleFeet.setOnClickListener {
            toggleFeet.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.bg_rectangle_cornered_4dp
                )
            )
            toggleCm.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            toggleCm.setTextColor(R.color.black)
            toggleFeet.setTextColor(R.color.dark_red)
            constrainCm.visibility = View.GONE
            constrainFeet.visibility = View.VISIBLE
            heightUnit = "inch"
        }

        toggleCm.setOnClickListener {
            toggleCm.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.bg_rectangle_cornered_4dp
                )
            )
            toggleFeet.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            toggleCm.setTextColor(R.color.dark_red)
            toggleFeet.setTextColor(R.color.black)
            constrainFeet.visibility = View.GONE
            constrainCm.visibility = View.VISIBLE
            heightUnit = "cm"

        }

        var inch = 0
        var feet = 0
        var cm = 0

        htIncrement.setOnClickListener {
            if (heightUnit == "cm") {
                cm += 1
                htInputCm.setText(cm.toString())
            } else if (heightUnit == "inch") {
                if (inch >= 11) {
                    feet += 1
                    inch = 0
                    htInputFeet.setText(feet.toString())
                    htInputInch.setText(inch.toString())
                } else {
                    inch += 1
                    htInputInch.setText(inch.toString())
                }
            }
        }

        htDecrement.setOnClickListener {
            if (heightUnit == "cm") {
                if (cm > 1) {
                    cm -= 1
                    htInputCm.setText(cm.toString())
                }
            } else if (heightUnit == "inch") {
                if (feet >= 1 || inch > 1)
                    if (inch <= 1) {
                        feet -= 1
                        inch = 11
                        htInputFeet.setText(feet.toString())
                        htInputInch.setText(inch.toString())
                    } else {
                        inch -= 1
                        htInputInch.setText(inch.toString())
                    }
            }
        }

        htDone.setOnClickListener {
            if (heightUnit == "cm") {
                if (htInputCm.text.isNotBlank()) {
                    height = Integer.parseInt(htInputCm.text.toString())
                    binding.heightValue.text = "$height cm"
                    binding.heightValue.setTextColor(R.color.black)
                    dialogHeight.dismiss()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please Enter Height Properly",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (heightUnit == "inch") {
                if (htInputInch.text.isNotBlank() && htInputFeet.text.isNotBlank()) {
                    height =
                        (Integer.parseInt(htInputFeet.text.toString()) * 12) + Integer.parseInt(
                            htInputInch.text.toString()
                        )
                    binding.heightValue.text =
                        htInputFeet.text.toString() + " FEET  " + Integer.parseInt(htInputInch.text.toString()) + " INCH"
                    binding.heightValue.setTextColor(R.color.black)
                    dialogHeight.dismiss()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please Enter Height Properly",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            Toast.makeText(requireContext(), "$height $heightUnit", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToNavHome() {
        val action = PersonalDetailsFragmentDirections.actionPersonalDetailsFragmentToNavHome()
        navController.navigate(action)
    }
}
