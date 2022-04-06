package tube.chikichiki.sako.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import tube.chikichiki.sako.R
import tube.chikichiki.sako.model.Video
import java.util.*

class VideoAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var videoViewClick:VideoViewClick?=null

    inner class VideoHolder(view: View): RecyclerView.ViewHolder(view){
        val banner: ImageView = itemView.findViewById(R.id.video_banner)
        val videoName: TextView = itemView.findViewById(R.id.video_name)
        val videoDuration:TextView=itemView.findViewById(R.id.video_duration)



    }
    inner class LoaderHolder(view: View): RecyclerView.ViewHolder(view)

    inner class EndOfVideosHolder(view: View):RecyclerView.ViewHolder(view)

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

    override fun getItemViewType(position: Int): Int {
        return diff.currentList[position].getUsedLayout()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)


        val holder:RecyclerView.ViewHolder = when (viewType) {
            R.layout.list_item_video -> {
                VideoHolder(view)
            }
            R.layout.list_item_loader -> {
                LoaderHolder(view)
            }
            else -> {
                EndOfVideosHolder(view)
            }
        }

        //set on click listener for videos
        view.setOnClickListener {
            val videoItem:Video = diff.currentList[holder.bindingAdapterPosition]
            videoViewClick?.onVideoClick(
                videoItem.uuid,
                videoItem.name,
                videoItem.description
            )
        }

        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val videoItem = diff.currentList[position]
        when(holder.itemViewType)
        {
            R.layout.list_item_video->{
                val videoHolder = holder as VideoHolder
                videoHolder.apply {
                    Glide.with(itemView.context).load(videoItem.getFullThumbnailPath()).format(DecodeFormat.PREFER_RGB_565).into(banner)
                    videoName.text=videoItem.name
                    videoDuration.text=videoItem.getFormattedDuration()
                }

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