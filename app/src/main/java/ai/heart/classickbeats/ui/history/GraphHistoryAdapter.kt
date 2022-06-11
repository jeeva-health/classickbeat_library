package ai.heart.classickbeats.ui.history

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.ItemviewGraphHistoryBinding
import ai.heart.classickbeats.databinding.ItemviewGraphHistoryDateBinding
import ai.heart.classickbeats.model.TimelineItem
import ai.heart.classickbeats.model.LogType
import ai.heart.classickbeats.model.entity.*
import ai.heart.classickbeats.shared.util.toTimeString
import ai.heart.classickbeats.shared.util.toTimeString2
import ai.heart.classickbeats.utils.setSafeOnClickListener
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class GraphHistoryAdapter constructor(
    private val context: Context,
    private val itemClickListener: (BaseLogEntity) -> Unit
) :
    ListAdapter<TimelineItem, RecyclerView.ViewHolder>
        (HistoryItemDiffCallback()) {

    companion object {
        const val LOG_ITEM = 0
        const val DATE_ITEM = 1
    }

    class HistoryItemViewHolder private constructor(
        val binding: ItemviewGraphHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            context: Context,
            itemData: BaseLogEntity,
            itemClickListener: (BaseLogEntity) -> Unit
        ) {
            val value: String
            val unit: String
            val time: String?
            when (itemData.type) {
                LogType.BloodPressure -> {
                    val pressureLogEntity = itemData as PressureLogEntity
                    val systolic = pressureLogEntity.systolic
                    val diastolic = pressureLogEntity.diastolic
                    value = "$systolic/$diastolic"
                    unit = context.getString(R.string.mmhg)
                    time = itemData.timeStamp?.toTimeString()
                    binding.clickArrow.visibility = View.GONE
                    binding.stressTag.visibility = View.GONE
                }
                LogType.GlucoseLevel -> {
                    val glucoseLogEntity = itemData as GlucoseLogEntity
                    value = glucoseLogEntity.glucoseLevel.toString()
                    unit = context.getString(R.string.mg_dl)
                    time = itemData.timeStamp?.toTimeString()
                    binding.clickArrow.visibility = View.GONE
                    binding.stressTag.visibility = View.GONE
                }
                LogType.WaterIntake -> {
                    val waterLogEntity = itemData as WaterLogEntity
                    value = waterLogEntity.quantity.toString()
                    unit = context.getString(R.string.ltrs)
                    time = itemData.timeStamp?.toTimeString()
                    binding.clickArrow.visibility = View.GONE
                    binding.stressTag.visibility = View.GONE
                }
                LogType.Weight -> {
                    val weightLogEntity = itemData as WeightLogEntity
                    value = weightLogEntity.weight.toString()
                    unit = context.getString(R.string.kg)
                    time = itemData.timeStamp?.toTimeString()
                    binding.clickArrow.visibility = View.GONE
                    binding.stressTag.visibility = View.GONE
                }
                LogType.PPG -> {
                    val ppgEntity = itemData as PPGEntity
                    value = ppgEntity.hr?.toInt().toString()
                    unit = context.getString(R.string.bpm)
                    time = itemData.timeStamp?.toTimeString2()
                    binding.clickArrow.visibility = View.VISIBLE
                    binding.stressTag.visibility = View.VISIBLE
                    when (ppgEntity.stressLevel ?: 0) {
                        1 -> {
                            binding.stressTag.text = context.getString(R.string.low_stress)
                            binding.stressTag.backgroundTintList =
                                ColorStateList.valueOf(context.getColor(R.color.moderate_green_2))
                        }
                        2 -> {
                            binding.stressTag.text = context.getString(R.string.normal_stress)
                            binding.stressTag.backgroundTintList =
                                ColorStateList.valueOf(context.getColor(R.color.vivid_yellow))
                        }
                        3 -> {
                            binding.stressTag.text = context.getString(R.string.high_stress)
                            binding.stressTag.backgroundTintList =
                                ColorStateList.valueOf(context.getColor(R.color.bright_red_3))
                        }
                        else -> {
                            binding.stressTag.visibility = View.GONE
                        }
                    }
                }
                LogType.Medicine -> TODO()
            }
            binding.value.text = value
            binding.unit.text = unit
            binding.time.text = time
            binding.root.setSafeOnClickListener {
                itemClickListener.invoke(itemData)
            }
        }

        companion object {
            fun from(parent: ViewGroup): HistoryItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemviewGraphHistoryBinding.inflate(layoutInflater, parent, false)
                return HistoryItemViewHolder(binding)
            }
        }
    }

    class DateItemViewHolder private constructor(val binding: ItemviewGraphHistoryDateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(itemData: String) {
            binding.date.text = itemData
        }

        companion object {
            fun from(parent: ViewGroup): DateItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemviewGraphHistoryDateBinding.inflate(layoutInflater, parent, false)
                return DateItemViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == LOG_ITEM) {
            HistoryItemViewHolder.from(parent)
        } else {
            DateItemViewHolder.from(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            when (it) {
                is TimelineItem.LogItem -> (holder as HistoryItemViewHolder).bind(
                    context,
                    it.logEntity,
                    itemClickListener
                )
                is TimelineItem.DateItem -> (holder as DateItemViewHolder).bind(it.date)
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