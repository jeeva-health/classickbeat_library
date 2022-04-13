package ai.heart.classickbeats.ui.ppg.fragment.my_health

import ai.heart.classickbeats.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BoardAdapter constructor(
    private val context: Context,
    private val boardModelList: List<BoardModel>,
    private val itemClickListener: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val uu = LayoutInflater.from(parent.context).inflate(R.layout.itemview_my_health_board, parent,false)
        return BoardViewHolder(uu)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BoardViewHolder).setBoardData(boardModelList[position])
    }

    override fun getItemCount(): Int {
        return boardModelList.size
    }

    class BoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon: ImageView? = itemView.findViewById(R.id.boardIcon)
        private val image: ImageView? = itemView.findViewById(R.id.boardImage)
        private val action: TextView? = itemView.findViewById(R.id.boardAction)
        private val value: TextView? = itemView.findViewById(R.id.boardValue)
        private val unit: TextView? = itemView.findViewById(R.id.boardUnit)
        fun setBoardData(model: BoardModel) {
            icon!!.setImageResource(model.icon)
            if (model.viewImage) {
                image!!.visibility = View.VISIBLE
            } else {
                image!!.visibility = View.GONE
            }

            action?.text = model.action
            value?.text = model.value
            unit?.text = model.unit
        }
    }

}