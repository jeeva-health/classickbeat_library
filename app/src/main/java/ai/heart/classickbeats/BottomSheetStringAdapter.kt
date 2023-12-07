package ai.heart.classickbeats

import ai.heart.classickbeats.databinding.ItemviewSingleStringBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class BottomSheetStringAdapter(
    private var textSize: Float = 14.0f,
    private val itemClickListener: (Int) -> Unit = {}
) :
    ListAdapter<String, BottomSheetStringAdapter.ItemViewHolder>(StringItemDiffCallback()) {

    private var textList: MutableList<TextView> = mutableListOf()

    fun updateTextSize(textSize: Float) {
        this.textSize = textSize
    }

    class ItemViewHolder private constructor(val binding: ItemviewSingleStringBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: String,
            textSize: Float,
            textList: MutableList<TextView>,
            itemClickListener: (Int) -> Unit
        ) {
            val textItem = binding.itemText
            textItem.text = item
            textItem.textSize = textSize
            textItem.setOnClickListener { selectedView ->
                textList.forEach {
                    it.isSelected = false
                }
                selectedView.isSelected = true
                itemClickListener.invoke(layoutPosition)
            }
            textList.add(textItem)
        }

        companion object {
            fun from(parent: ViewGroup): ItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemviewSingleStringBinding.inflate(layoutInflater, parent, false)
                return ItemViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder.from(parent)

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, textSize, textList, itemClickListener)
    }
}
