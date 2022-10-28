package tube.chikichiki.sako.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import tube.chikichiki.sako.R
import tube.chikichiki.sako.model.WatchLater
import java.util.*

class WatchLaterAdapter(private val watchLaterItems:List<WatchLater>):RecyclerView.Adapter<WatchLaterAdapter.WatchLaterHolder>() {
    private var watchLaterClick:WatchLaterClick? = null
    private var watchLaterRemoveClick:WatchLaterRemoveClick? = null

    inner class WatchLaterHolder(view:View):RecyclerView.ViewHolder(view){
        val banner: ImageView = itemView.findViewById(R.id.history_vertical_banner)
        val videoName: TextView = itemView.findViewById(R.id.history_vertical_name)
        val videoDuration: TextView = itemView.findViewById(R.id.history_vertical_duration)
        val historyRemoveBtn: ImageButton = itemView.findViewById(R.id.history_vertical_remove_btn)
    }

    fun setWatchLaterOnClickListener(clickListener:WatchLaterClick){
        watchLaterClick = clickListener
    }

    fun setWatchLaterRemoveOnClickListener(clickListener: WatchLaterRemoveClick){
        watchLaterRemoveClick = clickListener
    }

    interface WatchLaterRemoveClick{
        fun onRemoveClick(watchLater: WatchLater)
    }

    interface WatchLaterClick{
        fun onItemClick(videoId: UUID, videoName: String, videoDescription: String, previewPath:String, duration:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchLaterHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_vertical_history,parent,false)
        return WatchLaterHolder(view)
    }

    override fun onBindViewHolder(holder: WatchLaterHolder, position: Int) {
        val watchLaterItem = watchLaterItems[position]
        holder.apply {
            Glide.with(itemView.context).load(watchLaterItem.getFullThumbnailPath()).format(
                DecodeFormat.PREFER_RGB_565).into(banner)
            videoName.text=watchLaterItem.name
            videoDuration.text=watchLaterItem.getFormattedDuration()

            //video on click listener
            itemView.setOnClickListener {
                watchLaterClick?.onItemClick(watchLaterItem.uuid,watchLaterItem.name,watchLaterItem.description,watchLaterItem.previewPath,watchLaterItem.duration)
            }

            //remove on click listener
            historyRemoveBtn.setOnClickListener {
                watchLaterRemoveClick?.onRemoveClick(watchLaterItem)
            }
        }


    }

    override fun getItemCount(): Int {
        return watchLaterItems.size
    }


}