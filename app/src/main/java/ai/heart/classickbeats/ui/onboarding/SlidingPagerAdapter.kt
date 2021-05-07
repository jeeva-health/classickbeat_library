package ai.heart.classickbeats.ui.onboarding

import ai.heart.classickbeats.databinding.ItemviewOnboardingBinding
import ai.heart.classickbeats.model.OnBoardingModel
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView

class SlidingPagerAdapter(
    private val context: Context,
    private val dataList: List<OnBoardingModel>
) :
    RecyclerView.Adapter<SlidingPagerAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(context, dataList[position])
    }

    class ItemViewHolder private constructor(val binding: ItemviewOnboardingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(context: Context, itemData: OnBoardingModel) {
            val resource = context.resources
            binding.root.background =
                ResourcesCompat.getDrawable(resource, itemData.backgroundDrawableId, null)
            binding.icon.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resource,
                    itemData.iconDrawableId,
                    null
                )
            )
            binding.message.text = itemData.message
        }

        companion object {
            fun from(parent: ViewGroup): ItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemviewOnboardingBinding.inflate(layoutInflater, parent, false)
                return ItemViewHolder(binding)
            }
        }
    }
}