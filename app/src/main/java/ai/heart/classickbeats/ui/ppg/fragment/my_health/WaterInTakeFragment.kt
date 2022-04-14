package ai.heart.classickbeats.ui.ppg.fragment.my_health

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentWaterInTakeBinding
import ai.heart.classickbeats.utils.viewBinding
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager


class WaterInTakeFragment : Fragment(R.layout.fragment_water_in_take) {

    private val binding by viewBinding(FragmentWaterInTakeBinding::bind)
    var xWater: Int = 0

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        waterIntakeChange()

        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.witRecyclerView.layoutManager = layoutManager

        val modelList: MutableList<WaterIntakeItemModel> = java.util.ArrayList()
        modelList.add(WaterIntakeItemModel(R.drawable.water_bottel, "Water", false))
        modelList.add(WaterIntakeItemModel(R.drawable.juice, "Juice", false))
        modelList.add(WaterIntakeItemModel(R.drawable.tea, "Tea", false))
        modelList.add(WaterIntakeItemModel(R.drawable.coffe_long, "Coffee", false))
        modelList.add(WaterIntakeItemModel(R.drawable.soda, "Soda", false))

        val waterIntakeItemAdapter = WaterIntakeItemAdapter(modelList)
        binding.witRecyclerView.adapter = waterIntakeItemAdapter
        waterIntakeItemAdapter.notifyDataSetChanged()



        binding.witSaveBtn.setOnClickListener {
            //
        }

        binding.witBackBtn.setOnClickListener {
            findNavController().navigate(WaterInTakeFragmentDirections.actionWaterIntakeFragmentToMyHealthFragment())
        }
    }

    fun waterIntakeChange() {
        binding.witWaterIndicaterText.text = "$xWater X Glass 250 ml"
        binding.witMinus.setOnClickListener {
            if (xWater > 0) {
                xWater -= 1
                binding.witWaterIndicaterText.text = "$xWater X Glass 250 ml"
            }
        }
        binding.witPlus.setOnClickListener {
            if (xWater < 100) {
                xWater += 1
                binding.witWaterIndicaterText.text = "$xWater X Glass 250 ml"
            }
        }
    }

}