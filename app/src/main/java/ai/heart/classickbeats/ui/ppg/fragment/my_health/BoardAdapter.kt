package ai.heart.classickbeats.ui.ppg.fragment.my_health

import ai.heart.classickbeats.R
import ai.heart.classickbeats.model.Date
import ai.heart.classickbeats.ui.common.compose.BarModel
import ai.heart.classickbeats.ui.common.compose.CustomBarChat
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BoardAdapter constructor(
    private val context: Context,
    private val boardModelList: List<BoardModel>,
    private val itemClickListener: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val uu = LayoutInflater.from(parent.context)
            .inflate(R.layout.itemview_my_health_board, parent, false)
        return BoardViewHolder(uu)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BoardViewHolder).setBoardData(
            context = context,
            model = boardModelList[position],
            itemClickListener = itemClickListener
        )
    }

    override fun getItemCount(): Int {
        return boardModelList.size
    }

    class BoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon: ImageView? = itemView.findViewById(R.id.boardIcon)
        private val frameLayout: FrameLayout? = itemView.findViewById(R.id.boardImage)
        private val action: TextView? = itemView.findViewById(R.id.boardAction)
        private val value: TextView? = itemView.findViewById(R.id.boardValue)
        private val unit: TextView? = itemView.findViewById(R.id.boardUnit)

        fun setBoardData(context: Context, model: BoardModel, itemClickListener: (String) -> Unit) {
            icon!!.setImageResource(model.icon)


            action?.text = model.action
            value?.text = model.value
            unit?.text = model.unit
            if (model.viewGraph) {
//                val view: View = LayoutInflater.from(context).inflate(R.layout.layout_bar_chat, null)
//                frameLayout?.addView(view)
                frameLayout!!.visibility = View.VISIBLE


                if (model.action == BoardModel.HEART_RATE) {
                    //todo
                } else if (model.action == BoardModel.WATER_INTAKE) {
                    val d1 = BarModel(2f, Date(3, 4, 2022))
                    val d2 = BarModel(6f, Date(4, 4, 2022))
                    val d3 = BarModel(4f, Date(5, 4, 2022))
                    val d4 = BarModel(3f, Date(6, 4, 2022))
                    val d5 = BarModel(4f, Date(7, 4, 2022))
                    val d6 = BarModel(6f, Date(8, 4, 2022))
                    val d7 = BarModel(3f, Date(9, 4, 2022))

                    val data: List<BarModel> = arrayListOf(d1, d2, d3, d4, d5, d6, d7)
                    val v = CustomBarChat(context = context, null, dataPoints = data)
                    frameLayout.addView(v)
                }
            } else {
                frameLayout!!.visibility = View.GONE
            }

            itemView.setOnClickListener {
                itemClickListener.invoke(model.action)
            }
        }
    }

}