package ai.heart.classickbeats.ui.wellness

import ai.heart.classickbeats.databinding.ItemviewMeditationBinding
import ai.heart.classickbeats.model.MeditationMedia
import ai.heart.classickbeats.utils.setSafeOnClickListener
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class MeditationAdapter constructor(
    private val context: Context,
    private val itemClickListener: (MeditationMedia) -> Unit
) :
    ListAdapter<MeditationMedia, MeditationAdapter.ItemViewHolder>(MeditationItemDiffCallback()) {

    class ItemViewHolder private constructor(val binding: ItemviewMeditationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            context: Context,
            item: MeditationMedia,
            itemClickListener: (MeditationMedia) -> Unit
        ) {
            binding.name.text = item.getCategoryName(context)
            binding.duration.text = item.getDurationString(context)
            binding.root.setSafeOnClickListener {
                itemClickListener.invoke(item)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemviewMeditationBinding.inflate(layoutInflater, parent, false)
                return ItemViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
        ItemViewHolder.from(parent)

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            holder.bind(context, it, itemClickListener)
        }
    }
}

class MeditationItemDiffCallback : DiffUtil.ItemCallback<MeditationMedia>() {
    override fun areItemsTheSame(oldItem: MeditationMedia, newItem: MeditationMedia): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: MeditationMedia, newItem: MeditationMedia): Boolean =
        oldItem.resourceUrl == newItem.resourceUrl && oldItem.id == newItem.id
}
