package ai.heart.classickbeats.ui.common.ui

import ai.heart.classickbeats.R
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import timber.log.Timber

class CustomSliderScale @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, maxValue:Int,re:MutableStateFlow<Int>
) : FrameLayout(context, attrs) {

    var reading = MutableStateFlow(0)

    init {
        val view = inflate(context, R.layout.layout_slider, this)
        val recyclerView=view.findViewById<RecyclerView>(R.id.recycle)

        val manager = LinearLayoutManager(context)
        manager.orientation = RecyclerView.HORIZONTAL

        recyclerView.layoutManager = manager
        recyclerView.adapter = Adapter(maxValue)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @SuppressLint("SetTextI18n")
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstItem: Int = manager.findFirstVisibleItemPosition()
                val lastItem: Int = manager.findLastCompletelyVisibleItemPosition() + 1

                re.value = (firstItem + (lastItem - firstItem) /2)

            }
        })
    }

    class Adapter(private val size: Int) :
        RecyclerView.Adapter<Adapter.viewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
            val view: View =
                LayoutInflater.from(parent.context).inflate(R.layout.item_sscale_slider, parent, false)
            return viewHolder(view)
        }

        override fun onBindViewHolder(holder: viewHolder, position: Int) {
            holder.setData(position)
        }

        override fun getItemCount(): Int {
            return size
        }

        inner class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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