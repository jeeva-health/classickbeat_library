package ai.heart.classickbeats.ui.history

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.ItemviewHistoryBinding
import ai.heart.classickbeats.databinding.ItemviewHistoryDateBinding
import ai.heart.classickbeats.model.LogType
import ai.heart.classickbeats.model.Timeline
import ai.heart.classickbeats.model.TimelineItem
import ai.heart.classickbeats.utils.setSafeOnClickListener
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class TimelineAdapter constructor(
    private val context: Context,
    private val itemClickListener: (Timeline) -> Unit
) :
    PagingDataAdapter<TimelineItem, RecyclerView.ViewHolder>(TimelineItemDiffCallback()) {

    companion object {
        const val LOG_ITEM = 0
        const val DATE_ITEM = 1
    }

    class TimelineViewHolder private constructor(val binding: ItemviewHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            context: Context,
            itemData: Timeline,
            itemClickListener: (Timeline) -> Unit
        ) {
            val title: String
            val value: String
            val unit: String
            when (itemData.model) {
                LogType.BloodPressure -> {
                    title = context.getString(R.string.blood_pressure)
                    val systolic = itemData.systolicAvg
                    val diastolic = itemData.diastolicAvg
                    value = "$systolic/$diastolic"
                    unit = context.getString(R.string.mmhg)
                    binding.stressTag.visibility = View.GONE
                }
                LogType.GlucoseLevel -> {
                    title = context.getString(R.string.blood_glucose_level)
                    value = "${itemData.avgValue}"
                    unit = context.getString(R.string.mg_dl)
                    binding.stressTag.visibility = View.GONE
                }
                LogType.WaterIntake -> {
                    title = context.getString(R.string.water_intake)
                    value = "${itemData.avgValue}"
                    unit = context.getString(R.string.ltrs)
                    binding.stressTag.visibility = View.GONE
                }
                LogType.Weight -> {
                    title = context.getString(R.string.weight)
                    value = "${itemData.avgValue}"
                    unit = context.getString(R.string.kg)
                    binding.stressTag.visibility = View.GONE
                }
                LogType.PPG -> {
                    title = context.getString(R.string.heart_rate)
                    value = "${itemData.hrAvg}"
                    unit = context.getString(R.string.bpm)
                    binding.stressTag.visibility = View.GONE
                }
                LogType.Medicine -> throw Exception("Not handled")
            }
            binding.title.text = title
            binding.value.text = value
            binding.unit.text = unit
            binding.root.setSafeOnClickListener {
                itemClickListener.invoke(itemData)
            }
        }

        companion object {
            fun from(parent: ViewGroup): TimelineViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemviewHistoryBinding.inflate(layoutInflater, parent, false)
                return TimelineViewHolder(binding)
            }
        }
    }

    class DateViewHolder private constructor(val binding: ItemviewHistoryDateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(itemData: String) {
            binding.date.text = itemData
        }

        companion object {
            fun from(parent: ViewGroup): DateViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemviewHistoryDateBinding.inflate(layoutInflater, parent, false)
                return DateViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == LOG_ITEM) {
            TimelineViewHolder.from(parent)
        } else {
            DateViewHolder.from(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            when (it) {
                is TimelineItem.LogItem -> (holder as TimelineViewHolder).bind(
                    context,
                    it.timeline,
                    itemClickListener
                )
                is TimelineItem.DateItem -> (holder as DateViewHolder).bind(it.date)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is TimelineItem.LogItem -> LOG_ITEM
            is TimelineItem.DateItem -> DATE_ITEM
            null -> throw UnsupportedOperationException("Unknown view")
        }
    }
}

class TimelineItemDiffCallback : DiffUtil.ItemCallback<TimelineItem>() {
    override fun areItemsTheSame(oldItem: TimelineItem, newItem: TimelineItem): Boolean =
        if (oldItem is TimelineItem.LogItem && newItem is TimelineItem.LogItem) {
            val old = oldItem.timeline
            val new = newItem.timeline
            old.date == new.date && old.type == new.type && old.model == new.model
        } else if (oldItem is TimelineItem.DateItem && newItem is TimelineItem.DateItem) {
            val old = oldItem.date
            val new = newItem.date
            old == new
        } else {
            false
        }

    override fun areContentsTheSame(oldItem: TimelineItem, newItem: TimelineItem): Boolean =
        if (oldItem is TimelineItem.LogItem && newItem is TimelineItem.LogItem) {
            val old = oldItem.timeline
            val new = newItem.timeline
            old.date == new.date && old.type == new.type && old.model == new.model
        } else if (oldItem is TimelineItem.DateItem && newItem is TimelineItem.DateItem) {
            val old = oldItem.date
            val new = newItem.date
            old == new
        } else {
            false
        }
}