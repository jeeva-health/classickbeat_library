package ai.heart.classickbeats.ui.ppg.fragment.my_health

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentMyHealthBinding
import ai.heart.classickbeats.ui.ppg.fragment.my_health.VitalsModel.Companion.FUNCTION_BLOOD_PRESSURE
import ai.heart.classickbeats.ui.ppg.fragment.my_health.VitalsModel.Companion.FUNCTION_HEART
import ai.heart.classickbeats.ui.ppg.fragment.my_health.VitalsModel.Companion.FUNCTION_INTAKE
import ai.heart.classickbeats.ui.ppg.fragment.my_health.VitalsModel.Companion.FUNCTION_WEIGHT
import ai.heart.classickbeats.ui.ppg.fragment.my_health.VitalsModel.Companion.RECENT_VITALS
import ai.heart.classickbeats.ui.ppg.fragment.my_health.VitalsModel.Companion.RECENT_VITALS_DAY
import ai.heart.classickbeats.ui.ppg.fragment.my_health.VitalsModel.Companion.UPCOMING_VITALS
import ai.heart.classickbeats.utils.viewBinding
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class MyHealthFragment : Fragment(R.layout.fragment_my_health) {

    private val binding by viewBinding(FragmentMyHealthBinding::bind)

    private lateinit var navController: NavController

    private lateinit var mhUpcomingVitalsAdapter: VitalsAdapter

    private lateinit var mhRecentVitalsAdapter: VitalsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        val boardManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.mhBoardRecycleview.layoutManager = boardManager

        val upcomingManager = LinearLayoutManager(context)
        upcomingManager.orientation = LinearLayoutManager.VERTICAL
        binding.mhUpcomingRecycleview.layoutManager = upcomingManager

        val recentManager = LinearLayoutManager(context)
        recentManager.orientation = LinearLayoutManager.VERTICAL
        binding.mhRecentRecycleview.layoutManager = recentManager

        //board
        val boardModelList: MutableList<BoardModel> = java.util.ArrayList()
        boardModelList.add(
            BoardModel(
                R.drawable.blood_pressure,
                BoardModel.BLOOD_PRESSURE,
                "85/75",
                "MMHG",
                false
            )
        )
        boardModelList.add(
            BoardModel(
                R.drawable.heart_rate,
                BoardModel.HEART_RATE,
                "92",
                "BPM",
                true
            )
        )
        boardModelList.add(
            BoardModel(
                R.drawable.your_weight,
                BoardModel.BODY_WEIGHT,
                "75",
                "KG",
                false
            )
        )
        boardModelList.add(
            BoardModel(
                R.drawable.water_intake,
                BoardModel.WATER_INTAKE,
                "1.95",
                "LITERS",
                true
            )
        )
        boardModelList.add(
            BoardModel(
                R.drawable.blood_glucose,
                BoardModel.BLOOD_GLUCOSE,
                "200",
                "MG/DL",
                false
            )
        )

        val boardAdapter = BoardAdapter(requireContext(), boardModelList, boardItemClickListener())
        binding.mhBoardRecycleview.adapter = boardAdapter

        //upcoming vitals
        val upcomingVitalModelList: MutableList<VitalsModel> = java.util.ArrayList()
        upcomingVitalModelList.add(
            VitalsModel(
                UPCOMING_VITALS,
                R.drawable.heart_rate,
                "Heart Rate",
                "7:30 AM",
                "Measure",
                FUNCTION_HEART
            )
        )
        upcomingVitalModelList.add(
            VitalsModel(
                UPCOMING_VITALS,
                R.drawable.water_intake,
                "Water Intake",
                "8:30 AM",
                "Intake",
                FUNCTION_INTAKE
            )
        )
        upcomingVitalModelList.add(
            VitalsModel(
                UPCOMING_VITALS,
                R.drawable.blood_pressure,
                "Blood Pressure",
                "9:00 AM",
                "Measure",
                FUNCTION_BLOOD_PRESSURE
            )
        )
        upcomingVitalModelList.add(
            VitalsModel(
                UPCOMING_VITALS,
                R.drawable.weight,
                "Body Weight",
                "2:30 PM",
                "Measure",
                FUNCTION_WEIGHT
            )
        )
        mhUpcomingVitalsAdapter =
            VitalsAdapter(
                requireActivity().applicationContext,
                upcomingVitalModelList,
                itemClickListener()
            )
        binding.mhUpcomingRecycleview.adapter = mhUpcomingVitalsAdapter

        //recent vitals
        val recentVitalsModelList: MutableList<VitalsModel> = java.util.ArrayList()
        recentVitalsModelList.add(VitalsModel(RECENT_VITALS_DAY, "TODAY"))
        recentVitalsModelList.add(
            VitalsModel(
                RECENT_VITALS,
                R.drawable.blood_pressure,
                "Blood Pressure",
                "141/90",
                "MMHG",
                "7:30 AM",
                FUNCTION_BLOOD_PRESSURE
            )
        )
        recentVitalsModelList.add(
            VitalsModel(
                RECENT_VITALS,
                R.drawable.weight,
                "Body Weight",
                "75",
                "KG",
                "7:30 AM",
                FUNCTION_WEIGHT
            )
        )
        mhRecentVitalsAdapter =
            VitalsAdapter(
                requireContext().applicationContext,
                recentVitalsModelList,
                itemClickListener()
            )
        binding.mhRecentRecycleview.adapter = mhRecentVitalsAdapter


    }

    private fun boardItemClickListener() = fun(action: String) {
        when (action) {
            BoardModel.HEART_RATE -> {
                findNavController().navigate(MyHealthFragmentDirections.actionMyHealthFragmentToHeartRateFragment())
            }
            BoardModel.BODY_WEIGHT -> {
                findNavController().navigate(MyHealthFragmentDirections.actionMyHealthFragmentToSaveWeightFragment())
            }
            BoardModel.WATER_INTAKE -> {
                findNavController().navigate(MyHealthFragmentDirections.actionMyHealthFragmentToWaterIntakeFragment())
            }
            BoardModel.BLOOD_PRESSURE -> {
                findNavController().navigate(MyHealthFragmentDirections.actionMyHealthFragmentToBloodPressureFragment())
            }
            BoardModel.BLOOD_GLUCOSE -> {
                findNavController().navigate(MyHealthFragmentDirections.actionMyHealthFragmentToBloodGlucoseFragment())
            }
            else -> {
                //todo nothing
                Toast.makeText(context, "selection error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun itemClickListener() = fun(function: Int) {
        when (function) {
            FUNCTION_HEART -> {
                findNavController().navigate(MyHealthFragmentDirections.actionMyHealthFragmentToHeartRateFragment())
            }
            FUNCTION_WEIGHT -> {
                findNavController().navigate(MyHealthFragmentDirections.actionMyHealthFragmentToSaveWeightFragment())
            }
            FUNCTION_INTAKE -> {
                findNavController().navigate(MyHealthFragmentDirections.actionMyHealthFragmentToWaterIntakeFragment())
            }
            FUNCTION_BLOOD_PRESSURE -> {
                findNavController().navigate(MyHealthFragmentDirections.actionMyHealthFragmentToBloodPressureFragment())
            }
            else -> {
                //todo nothing
                Toast.makeText(context, "elsed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}