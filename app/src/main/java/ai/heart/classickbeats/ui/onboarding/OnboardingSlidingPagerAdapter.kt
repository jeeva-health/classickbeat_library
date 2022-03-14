package ai.heart.classickbeats.ui.onboarding

import ai.heart.classickbeats.databinding.ItemviewOnboardingBinding
import ai.heart.classickbeats.model.OnboardingModel
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class OnboardingSlidingPagerAdapter(
    private val context: Context,
    private val onboardingModelList: List<OnboardingModel>
) : RecyclerView.Adapter<OnboardingSlidingPagerAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return onboardingModelList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(context, onboardingModelList[position])
    }

    class ItemViewHolder(val binding: ItemviewOnboardingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            context: Context,
            item: OnboardingModel,
        ) {
            Glide.with(context).load(item.imageDrawableId).into(binding.onboardImage)
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
