package ai.heart.classickbeats.ui.ppg.fragment.my_health

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentSaveWeightBinding
import ai.heart.classickbeats.utils.viewBinding
import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController


class SaveWeightFragment : Fragment(R.layout.fragment_save_weight) {

    private val binding by viewBinding(FragmentSaveWeightBinding::bind)
    private var unit = "KG"
    private var xWeight = 0


    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.swKg.setOnClickListener {
            unit = "KG"
            binding.swKg.setBackgroundResource(R.drawable.bg_rectangle_16)
            binding.swKg.setTextColor(R.color.pale_red)
            binding.swPound.setTextColor(R.color.charcoal_grey)
            binding.swPound.setBackgroundResource(R.color.transparent)
            binding.swUnit.text = unit
        }
        binding.swPound.setOnClickListener {
            unit = "POUND"
            binding.swPound.setBackgroundResource(R.drawable.bg_rectangle_16)
            binding.swPound.setTextColor(R.color.pale_red)
            binding.swKg.setTextColor(R.color.charcoal_grey)
            binding.swKg.setBackgroundResource(R.color.transparent)
            binding.swUnit.text = unit
        }

        binding.swWeight.setText("0")

        binding.swMinus.setOnClickListener {
            if (xWeight>0){
                xWeight -=1
                binding.swWeight.setText("$xWeight")
            }
        }

        binding.swPlus.setOnClickListener {
            if (xWeight<200){
                xWeight +=1
                binding.swWeight.setText("$xWeight")
            }
        }

        binding.swSaveBtn.setOnClickListener {
            Toast.makeText(requireContext(),"$xWeight $unit",Toast.LENGTH_SHORT).show()
        }

        binding.swBack.setOnClickListener {
            findNavController().navigate(SaveWeightFragmentDirections.actionSaveWeightFragmentToMyHealthFragment())
        }

    }
}