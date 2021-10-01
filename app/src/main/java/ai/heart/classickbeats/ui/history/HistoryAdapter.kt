package ai.heart.classickbeats.ui.history

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.ItemviewHistoryBinding
import ai.heart.classickbeats.databinding.ItemviewHistoryDateBinding
import ai.heart.classickbeats.model.HistoryItem
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
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter constructor(
    private val context: Context,
    private val itemClickListener: (BaseLogEntity) -> Unit
) :
    PagingDataAdapter<HistoryItem, RecyclerView.ViewHolder>(HistoryItemDiffCallback()) {

    companion object {
        const val LOG_ITEM = 0
        const val DATE_ITEM = 1
    }

    class HistoryItemViewHolder private constructor(val binding: ItemviewHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            context: Context,
            itemData: BaseLogEntity,
            itemClickListener: (BaseLogEntity) -> Unit
        ) {
            val title: String
            val value: String
            val unit: String
            var time: String? = null
            when (itemData.type) {
                LogType.BloodPressure -> {
                    val bpLogEntity = itemData as BpLogEntity
                    title = context.getString(R.string.blood_pressure)
                    val systolic = bpLogEntity.systolic
                    val diastolic = bpLogEntity.diastolic
                    value = "$systolic/$diastolic"
                    unit = context.getString(R.string.mmhg)
                    time = itemData.timeStamp?.toTimeString()
                    binding.clickArrow.visibility = View.GONE
                    binding.stressTag.visibility = View.GONE
                }
                LogType.GlucoseLevel -> {
                    val glucoseLogEntity = itemData as GlucoseLogEntity
                    title = context.getString(R.string.blood_glucose_level)
                    value = glucoseLogEntity.glucoseLevel.toString()
                    unit = context.getString(R.string.mg_dl)
                    time = itemData.timeStamp?.toTimeString()
                    binding.clickArrow.visibility = View.GONE
                    binding.stressTag.visibility = View.GONE
                }
                LogType.WaterIntake -> {
                    val waterLogEntity = itemData as WaterLogEntity
                    title = context.getString(R.string.water_intake)
                    value = waterLogEntity.quantity.toString()
                    unit = context.getString(R.string.ltrs)
                    time = itemData.timeStamp?.toTimeString()
                    binding.clickArrow.visibility = View.GONE
                    binding.stressTag.visibility = View.GONE
                }
                LogType.Weight -> {
                    val weightLogEntity = itemData as WeightLogEntity
                    title = context.getString(R.string.weight)
                    value = weightLogEntity.weight.toString()
                    unit = context.getString(R.string.kg)
                    time = itemData.timeStamp?.toTimeString()
                    binding.clickArrow.visibility = View.GONE
                    binding.stressTag.visibility = View.GONE
                }
                LogType.PPG -> {
                    val ppgEntity = itemData as PPGEntity
                    title = context.getString(R.string.heart_rate)
                    value = ppgEntity.hr?.toInt().toString()
                    unit = context.getString(R.string.bpm)
                    time = itemData.timeStamp?.toTimeString2()
                    binding.clickArrow.visibility = View.VISIBLE
                    binding.stressTag.visibility = View.VISIBLE
                    when (ppgEntity.stressLevel ?: 1) {
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
                        else -> {
                            binding.stressTag.text = context.getString(R.string.high_stress)
                            binding.stressTag.backgroundTintList =
                                ColorStateList.valueOf(context.getColor(R.color.bright_red_3))
                        }
                    }
                }
                LogType.Medicine -> TODO()
            }
            binding.title.text = title
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
                val binding = ItemviewHistoryBinding.inflate(layoutInflater, parent, false)
                return HistoryItemViewHolder(binding)
            }
        }
    }

    class DateItemViewHolder private constructor(val binding: ItemviewHistoryDateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(itemData: String) {
            binding.date.text = itemData
        }

        companion object {
            fun from(parent: ViewGroup): DateItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemviewHistoryDateBinding.inflate(layoutInflater, parent, false)
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
                is HistoryItem.LogItem -> (holder as HistoryItemViewHolder).bind(
                    context,
                    it.logEntity,
                    itemClickListener
                )
                is HistoryItem.DateItem -> (holder as DateItemViewHolder).bind(it.date)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is HistoryItem.LogItem -> LOG_ITEM
            is HistoryItem.DateItem -> DATE_ITEM
            null -> throw UnsupportedOperationException("Unknown view")
        }
    }
}

class HistoryItemDiffCallback : DiffUtil.ItemCallback<HistoryItem>() {
    override fun areItemsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
        return if (oldItem is HistoryItem.LogItem && newItem is HistoryItem.LogItem) {
            val old = oldItem.logEntity
            val new = newItem.logEntity
            if (old.type == new.type) {
                when (old.type) {
                    LogType.BloodPressure -> old as BpLogEntity == new as BpLogEntity
                    LogType.GlucoseLevel -> old as GlucoseLogEntity == new as GlucoseLogEntity
                    LogType.WaterIntake -> old as WaterLogEntity == new as WaterLogEntity
                    LogType.Weight -> old as WeightLogEntity == new as WeightLogEntity
                    LogType.Medicine -> old as MedicineLogEntity == new as MedicineLogEntity
                    LogType.PPG -> old as PPGEntity == new as PPGEntity
                }
            } else {
                false
            }
        } else if (oldItem is HistoryItem.DateItem && newItem is HistoryItem.DateItem) {
            val old = oldItem.date
            val new = newItem.date
            old == new
        } else {
            false
        }
    }

    override fun areContentsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
        return if (oldItem is HistoryItem.LogItem && newItem is HistoryItem.LogItem) {
            val old = oldItem.logEntity
            val new = newItem.logEntity
            if (old.type == new.type) {
                when (old.type) {
                    LogType.BloodPressure -> old as BpLogEntity == new as BpLogEntity
                    LogType.GlucoseLevel -> old as GlucoseLogEntity == new as GlucoseLogEntity
                    LogType.WaterIntake -> old as WaterLogEntity == new as WaterLogEntity
                    LogType.Weight -> old as WeightLogEntity == new as WeightLogEntity
                    LogType.Medicine -> old as MedicineLogEntity == new as MedicineLogEntity
                    LogType.PPG -> old as PPGEntity == new as PPGEntity
                }
            } else {
                false
            }
        } else if (oldItem is HistoryItem.DateItem && newItem is HistoryItem.DateItem) {
            val old = oldItem.date
            val new = newItem.date
            old == new
        } else {
            false
        }
    }
}