package ai.heart.classickbeats.ui.ppg

import ai.heart.classickbeats.databinding.ItemviewScanTutorialBinding
import ai.heart.classickbeats.model.ScanTutorialModel
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ScanSlidingPagerAdapter(
    private val dataList: List<ScanTutorialModel>
) :
    RecyclerView.Adapter<ScanSlidingPagerAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    class ItemViewHolder private constructor(val binding: ItemviewScanTutorialBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(itemData: ScanTutorialModel) {
            Glide.with(binding.root).load(itemData.iconDrawableId).into(binding.image)
            binding.message.text = itemData.message
        }

        companion object {
            fun from(parent: ViewGroup): ItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemviewScanTutorialBinding.inflate(layoutInflater, parent, false)
                return ItemViewHolder(binding)
            }
        }
    }
}