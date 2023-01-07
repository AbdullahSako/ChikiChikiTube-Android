package tube.chikichiki.sako.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import tube.chikichiki.sako.R
import tube.chikichiki.sako.model.HistoryVideoInfo
import java.util.*

class HistoryHorizontalAdapter(private val historyItems:List<HistoryVideoInfo>):RecyclerView.Adapter<HistoryHorizontalAdapter.HistoryHolder>() {

    private var historyVideoOnClick: HistoryHorizontalAdapter.HistoryViewClick? = null

    inner class HistoryHolder(view:View):RecyclerView.ViewHolder(view){
        val banner:ImageView = itemView.findViewById(R.id.history_video_banner)
        val videoName:TextView = itemView.findViewById(R.id.history_video_name)
        val videoDuration:TextView = itemView.findViewById(R.id.history_video_duration)
    }

    fun setHistoryViewClickListener(clickListener: HistoryHorizontalAdapter.HistoryViewClick) {
        historyVideoOnClick = clickListener
    }

    interface HistoryViewClick {
        fun onItemClick(videoId: UUID, videoName: String, videoDescription: String, previewPath:String, duration:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_horizontal_history,parent,false)
        return HistoryHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryHolder, position: Int) {
        val historyItem= historyItems[position]

        holder.apply {
            Glide.with(itemView.context).load(historyItem.getFullThumbnailPath()).format(DecodeFormat.PREFER_RGB_565).into(banner)
            videoName.text=historyItem.name
            videoDuration.text=historyItem.getFormattedDuration()

            //on click listener
            itemView.setOnClickListener {
                historyVideoOnClick?.onItemClick(historyItem.uuid,historyItem.name,historyItem.description,historyItem.previewPath,historyItem.duration)
            }
        }

    }

    override fun getItemCount(): Int {
        return historyItems.size
    }
}