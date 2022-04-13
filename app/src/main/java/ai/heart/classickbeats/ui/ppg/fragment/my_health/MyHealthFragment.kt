package ai.heart.classickbeats.ui.ppg.fragment.my_health

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentMyHealthBinding
import ai.heart.classickbeats.ui.ppg.fragment.my_health.VitalsModel.Companion.FUNCTION_BLOOD_PRESSURE
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
        val boardModelList:MutableList<BoardModel> = java.util.ArrayList()
        boardModelList.add(BoardModel(R.drawable.female_icon,"Glucose Level", "200","ppm",false))
        boardModelList.add(BoardModel(R.drawable.female_icon,"Blood pressure", "200","ppm",true))
        boardModelList.add(BoardModel(R.drawable.your_weight,"Weight", "60","KG",false))
        boardModelList.add(BoardModel(R.drawable.female_icon,"Glucose Level", "200","ppm",true))

        val boardAdapter = BoardAdapter(requireContext(),boardModelList,boardItemClickListener())
        binding.mhBoardRecycleview.adapter = boardAdapter

        //upcoming vitals
        val upcomingVitalModelList: MutableList<VitalsModel> = java.util.ArrayList()
        upcomingVitalModelList.add(VitalsModel(UPCOMING_VITALS,  "92","Measure","Today", FUNCTION_BLOOD_PRESSURE ))
        upcomingVitalModelList.add(VitalsModel(UPCOMING_VITALS,  "92","Measure","Today", FUNCTION_BLOOD_PRESSURE ))
        upcomingVitalModelList.add(VitalsModel(UPCOMING_VITALS,  "92","Measure","Today", FUNCTION_BLOOD_PRESSURE ))
        upcomingVitalModelList.add(VitalsModel(UPCOMING_VITALS,  "92","Measure","Today", FUNCTION_BLOOD_PRESSURE ))
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
                "00",
                "Weight",
                "Today",
                "KG",
                VitalsModel.FUNCTION_WEIGHT
            )
        )
        recentVitalsModelList.add(
            VitalsModel(
                RECENT_VITALS,
                "3",
                "Weight",
                "Today",
                "lt",
                VitalsModel.FUNCTION_INTAKE
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

private fun boardItemClickListener() = fun(function: Int){
    //TODO
}

    private fun itemClickListener() = fun(function: Int) {
        Toast.makeText(context, "item clicked", Toast.LENGTH_SHORT).show()
        when (function) {
            FUNCTION_WEIGHT -> {
                navigateToSaveWeight()
            }
            FUNCTION_INTAKE -> {
                navigateToWaterInTake()
            }
            FUNCTION_BLOOD_PRESSURE -> {
                //todo
            }
            else -> {
                //todo nothing
                Toast.makeText(context, "elsed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToWaterInTake() {
        val action = MyHealthFragmentDirections.actionMyHealthFragmentToWaterIntakeFragment()
        findNavController().navigate(action)
    }

    private fun navigateToSaveWeight() {
        val action = MyHealthFragmentDirections.actionMyHealthFragmentToSaveWeightFragment()
        findNavController().navigate(action)
    }
}