package ai.heart.classickbeats.ui.ppg.fragment.my_health

import ai.heart.classickbeats.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WaterIntakeItemAdapter(private val waterIntakeItemModelList: List<WaterIntakeItemModel>) :
    RecyclerView.Adapter<WaterIntakeItemAdapter.ItemViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.itemview_water_in_take, parent,false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.setData(waterIntakeItemModelList[position])
    }

    override fun getItemCount(): Int {
        return waterIntakeItemModelList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.iv_wit_image)
        private val type: TextView = itemView.findViewById(R.id.iv_wit_type)
        fun setData(model: WaterIntakeItemModel) {
            image.setImageResource(model.image)
            type.text = model.type
            itemView.setOnClickListener() {

                if (!model.selected) {
                    model.selected = true
                    itemView.setBackgroundResource(R.drawable.bg_red_rect_6)
                } else {
                    model.selected = false
                    itemView.setBackgroundResource(R.drawable.bg_rectangle_6)
                }
            }
        }

    }
}

/**
 * //upcoming vitals
private val uvVitals: TextView? = null
private val uvAction: TextView? = null
private val uvTimeStamp: TextView? = null
private val uvIcon: ImageView? = null

//recent vitals day
private val rvDay: TextView? = null

//recent vitals
private val rvIcon: ImageView? = null
private val rvVitals: TextView? = null
private val rvReading: TextView? = null
private val rvUnit: TextView? = null
private val rvTimeStamp: TextView? = null
 */