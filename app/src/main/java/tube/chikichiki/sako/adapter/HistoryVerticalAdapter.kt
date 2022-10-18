package tube.chikichiki.sako.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import tube.chikichiki.sako.R
import tube.chikichiki.sako.model.HistoryVideoInfo
import java.util.*

class HistoryVerticalAdapter(private val historyItems:List<HistoryVideoInfo>):RecyclerView.Adapter<HistoryVerticalAdapter.HistoryVerticalHolder>() {

    private var historyVideoOnClick: HistoryVerticalAdapter.HistoryViewClick? = null
    private var historyRemoveClick:HistoryRemoveClick ? = null

    inner class HistoryVerticalHolder(view: View): RecyclerView.ViewHolder(view){
        val banner: ImageView = itemView.findViewById(R.id.history_vertical_banner)
        val videoName: TextView = itemView.findViewById(R.id.history_vertical_name)
        val videoDuration: TextView = itemView.findViewById(R.id.history_vertical_duration)
        val historyRemoveBtn:ImageButton = itemView.findViewById(R.id.history_vertical_remove_btn)
    }

    fun setHistoryViewClickListener(clickListener: HistoryVerticalAdapter.HistoryViewClick) {
        historyVideoOnClick = clickListener
    }

    fun setHistoryRemoveClickListener(clickListener: HistoryRemoveClick){
        historyRemoveClick = clickListener
    }

    interface HistoryRemoveClick{
        fun onRemoveClick(historyItem:HistoryVideoInfo)
    }

    interface HistoryViewClick {
        fun onItemClick(videoId: UUID, videoName: String, videoDescription: String, previewPath:String, duration:Int)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryVerticalAdapter.HistoryVerticalHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_vertical_history,parent,false)
        return HistoryVerticalHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryVerticalHolder, position: Int) {
        val historyItem= historyItems[position]

        holder.apply {
            Glide.with(itemView.context).load(historyItem.getFullThumbnailPath()).format(
                DecodeFormat.PREFER_RGB_565).into(banner)
            videoName.text=historyItem.name
            videoDuration.text=historyItem.getFormattedDuration()

            //video on click listener
            itemView.setOnClickListener {
                historyVideoOnClick?.onItemClick(historyItem.uuid,historyItem.name,historyItem.description,historyItem.previewPath,historyItem.duration)
            }

            //remove on click listener
            historyRemoveBtn.setOnClickListener {
                historyRemoveClick?.onRemoveClick(historyItem)
            }
        }

    }

    override fun getItemCount(): Int {
        return historyItems.size
    }


}