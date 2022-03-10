package tube.chikichiki.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tube.chikichiki.R
import tube.chikichiki.model.Video
import java.util.*

class VideoAdapter : RecyclerView.Adapter<VideoAdapter.VideoHolder>() {

    var videoViewClick:VideoViewClick?=null

    inner class VideoHolder(view: View): RecyclerView.ViewHolder(view){
        val banner: ImageView = itemView.findViewById(R.id.video_banner)
        val videoName: TextView = itemView.findViewById(R.id.video_name)
        val videoDuration:TextView=itemView.findViewById(R.id.video_duration)



    }

    fun setVideoViewClickListener(clickListener: VideoViewClick){
        videoViewClick=clickListener
    }

    private val diffCallback= object : DiffUtil.ItemCallback<Video>(){
        override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean {
            return oldItem.uuid == newItem.uuid
        }

        override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean {
            return oldItem.name == newItem.name
        }

    }

    private val diff = AsyncListDiffer(this,diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_video, parent, false)
        return VideoHolder(view)
    }

    override fun onBindViewHolder(holder: VideoHolder, position: Int) {

        val videoItem = diff.currentList[position]
        holder.apply {

            Glide.with(itemView.context).load(videoItem.getFullThumbnailPath()).into(banner)
            videoName.text=videoItem.name
            videoDuration.text=videoItem.getFormattedDuration()
            itemView.setOnClickListener {
                videoViewClick?.onVideoClick(videoItem.uuid, videoItem.name, videoItem.description)
            }
        }



    }

    override fun getItemCount(): Int {
        return diff.currentList.size
    }

    fun submitList(list:List<Video>){
        diff.submitList(list)
    }

    interface VideoViewClick{
        fun onVideoClick(videoId: UUID, videoName: String, videoDescription: String)
    }




}