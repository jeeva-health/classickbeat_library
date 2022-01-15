package ai.heart.classickbeats.ui.profile

import ai.heart.classickbeats.databinding.ItemviewReminderBinding
import ai.heart.classickbeats.model.Reminder
import ai.heart.classickbeats.model.toDisplayString
import ai.heart.classickbeats.utils.setSafeOnClickListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ReminderAdapter constructor(
    private val itemClickListener: (Reminder) -> Unit,
    private val toggleClickListener: (Reminder, Boolean) -> Unit
) :
    ListAdapter<Reminder, ReminderAdapter.ItemViewHolder>(ReminderItemDiffCallback()) {

    class ItemViewHolder private constructor(val binding: ItemviewReminderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            itemData: Reminder,
            itemClickListener: (Reminder) -> Unit,
            toggleClickListener: (Reminder, Boolean) -> Unit
        ) {
            binding.name.text = itemData.name
            if (itemData.isReminderSet) {
                binding.time.visibility = View.VISIBLE
                binding.time.text = itemData.time.toString()
                binding.time.visibility = View.VISIBLE
                binding.frequency.text = itemData.frequency.toDisplayString()
                binding.toggleSwitch.isChecked = itemData.isReminderActive
                binding.toggleSwitch.setOnCheckedChangeListener { _, isChecked ->
                    toggleClickListener.invoke(itemData, isChecked)
                }
                binding.notSet.visibility = View.GONE
            } else {
                binding.time.visibility = View.GONE
                binding.frequency.visibility = View.GONE
                binding.notSet.visibility = View.VISIBLE
            }
            binding.root.setSafeOnClickListener {
                itemClickListener.invoke(itemData)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemviewReminderBinding.inflate(layoutInflater, parent, false)
                return ItemViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
        ItemViewHolder.from(parent)

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            holder.bind(it, itemClickListener, toggleClickListener)
        }
    }
}

class ReminderItemDiffCallback : DiffUtil.ItemCallback<Reminder>() {
    override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder): Boolean =
        oldItem._id == newItem._id

    override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder): Boolean =
        oldItem._id == newItem._id &&
                oldItem.name == newItem.name &&
                oldItem.time == newItem.time &&
                newItem.frequency.toDisplayString() == oldItem.frequency.toDisplayString()
}