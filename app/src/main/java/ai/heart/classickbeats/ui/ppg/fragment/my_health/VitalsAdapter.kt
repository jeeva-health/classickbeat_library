package ai.heart.classickbeats.ui.ppg.fragment.my_health

import ai.heart.classickbeats.R
import ai.heart.classickbeats.ui.ppg.fragment.my_health.VitalsModel.Companion.RECENT_VITALS
import ai.heart.classickbeats.ui.ppg.fragment.my_health.VitalsModel.Companion.RECENT_VITALS_DAY
import ai.heart.classickbeats.ui.ppg.fragment.my_health.VitalsModel.Companion.UPCOMING_VITALS
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VitalsAdapter constructor(
    private val context: Context,
    private val vitalsModelList: List<VitalsModel>,
    private val itemClickListener: (Int) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private lateinit var recycledViewPool: RecyclerView.RecycledViewPool
    }

    init {
        recycledViewPool = RecyclerView.RecycledViewPool()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int

    ): RecyclerView.ViewHolder {

        return when (viewType) {
            UPCOMING_VITALS -> {
                val upcomingViewHolder = LayoutInflater.from(parent.context)
                    .inflate(R.layout.itemview_my_health_upcoming, parent, false)
                UpcomingViewHolder(upcomingViewHolder)
            }
            RECENT_VITALS_DAY -> {
                val recentDayViewHolder = LayoutInflater.from(parent.context)
                    .inflate(R.layout.itemview_my_health_recent_1, parent, false)
                RecentDayViewHolder(recentDayViewHolder)
            }
            else -> { //if (viewType ==  VitalsModel.RECENT_VITALS)
                val recentViewHolder = LayoutInflater.from(parent.context)
                    .inflate(R.layout.itemview_my_health_recent_2, parent, false)
                RecentViewHolder(recentViewHolder)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (vitalsModelList[position].type) {
            0 -> UPCOMING_VITALS
            1 -> RECENT_VITALS_DAY
            2 -> RECENT_VITALS
            else -> -1
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (vitalsModelList[position].type) {
            UPCOMING_VITALS -> (holder as UpcomingViewHolder).setUpcomingData(
                vitalsModelList[position],
                itemClickListener
            )
            RECENT_VITALS_DAY -> (holder as RecentDayViewHolder).setRecentDayData(
                vitalsModelList[position]
            )
            RECENT_VITALS -> (holder as RecentViewHolder).setRecentData(
                vitalsModelList[position],
                itemClickListener
            )
        }
    }

    override fun getItemCount(): Int {
        return vitalsModelList.size
    }

    //................... Up coming Vitals ......................//
    class UpcomingViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val uvIcon: ImageView? = itemView.findViewById(R.id.upcomingIcon);
        private val uvVitals: TextView? = itemView.findViewById(R.id.upcomingVitals);
        private val uvAction: TextView? = itemView.findViewById(R.id.upcomingBtn);
        private val uvTimeStamp: TextView? = itemView.findViewById(R.id.upcomingTimestamp);
        fun setUpcomingData(model: VitalsModel,    itemClickListener: (Int) -> Unit) {
            uvIcon?.setImageResource(model.icon)
            uvVitals?.text = model.name
            uvAction?.text = model.action
            uvTimeStamp?.text = model.timeStamp
            itemView.setOnClickListener {
                itemClickListener.invoke(model.function)
            }
        }
    }

    //................... Recent Day Vitals ......................//
    class RecentDayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val rvDay: TextView? = itemView.findViewById(R.id.recentDayValue);
        fun setRecentDayData(model: VitalsModel) {
            rvDay!!.text = model.timeStamp
        }
    }

    //................... Recent Vitals ......................//
    class RecentViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val rvIcon: ImageView? = itemView.findViewById(R.id.recentIcon);
        private val rvVitals: TextView? = itemView.findViewById(R.id.recentVitals);
        private val rvReading: TextView? = itemView.findViewById(R.id.recentValue);
        private val rvUnit: TextView? = itemView.findViewById(R.id.recentUnit);
        private val rvTimeStamp: TextView? = itemView.findViewById(R.id.recentTimestamp);
        fun setRecentData(
            model: VitalsModel,
            itemClickListener: (Int) -> Unit
        ) {

            rvIcon?.setImageResource(model.icon)
            rvVitals?.text = model.name
            rvReading?.text = model.reading
            rvUnit?.text = model.unit
            rvTimeStamp?.text = model.timeStamp
            itemView.setOnClickListener {
                itemClickListener.invoke(model.function)
            }
        }
    }


}