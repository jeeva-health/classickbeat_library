package ai.heart.classickbeats.ui.common.compose

import ai.heart.classickbeats.R
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

@SuppressLint("ViewConstructor")
class CustomSliderScale constructor(
    context: Context,
    currentReading: Int,
    maxValue: Int,
    onReadingChange: (Int) -> Unit
) : FrameLayout(context) {

    init {
        val view = inflate(context, R.layout.layout_slider, this)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycle)

        val manager = LinearLayoutManager(context)
        manager.orientation = RecyclerView.HORIZONTAL

        recyclerView.layoutManager = manager
        recyclerView.adapter = Adapter(maxValue)

        recyclerView.scrollToPosition(currentReading) //TODO Rajesh: Need to check this

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @SuppressLint("SetTextI18n")
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstItem: Int = manager.findFirstVisibleItemPosition()
                val lastItem: Int = manager.findLastCompletelyVisibleItemPosition() + 1
                onReadingChange.invoke(firstItem + (lastItem - firstItem) / 2)
            }
        })
    }

    class Adapter(private val size: Int) :
        RecyclerView.Adapter<Adapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_sscale_slider, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.setData(position)
        }

        override fun getItemCount(): Int {
            return size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val view: ImageView
            private val text: TextView
            fun setData(position: Int) {
                for (i in 0 until size) {
                    if (position % 5 == 0) {
                        val layoutParams = view.layoutParams
                        layoutParams.width = 7
                        layoutParams.height = 50
                        view.layoutParams = layoutParams
                        text.visibility = VISIBLE
                        text.text = position.toString()
                    } else {
                        val layoutParams = view.layoutParams
                        layoutParams.width = 7
                        layoutParams.height = 30
                        view.layoutParams = layoutParams
                        text.visibility = INVISIBLE
                    }
                }
            }

            init {
                view = itemView.findViewById(R.id.divider2)
                text = itemView.findViewById(R.id.textView2)
            }
        }
    }
}
