package tube.chikichiki.sako.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import tube.chikichiki.sako.R
import tube.chikichiki.sako.database.ChikiChikiDatabaseRepository
import tube.chikichiki.sako.model.Video
import java.util.*

class VideoAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var videoViewClick:VideoViewClick?=null

    inner class VideoHolder(view: View): RecyclerView.ViewHolder(view){
        val banner: ImageView = itemView.findViewById(R.id.video_banner)
        val videoName: TextView = itemView.findViewById(R.id.video_name)
        val videoDuration:TextView=itemView.findViewById(R.id.video_duration)
        val watchedTimeProgressBar:ProgressBar = itemView.findViewById(R.id.watchedProgressBar)



    }
    inner class LoaderHolder(view: View): RecyclerView.ViewHolder(view)

    inner class EndOfVideosHolder(view: View):RecyclerView.ViewHolder(view)

    fun setVideoViewClickListener(clickListener: VideoViewClick){
        videoViewClick=clickListener
    }

    private val diffCallback= object : DiffUtil.ItemCallback<Pair<Video,Long>>(){

        override fun areItemsTheSame(
            oldItem: Pair<Video, Long>,
            newItem: Pair<Video, Long>
        ): Boolean {
            return oldItem.first.uuid == newItem.first.uuid
        }

        override fun areContentsTheSame(
            oldItem: Pair<Video, Long>,
            newItem: Pair<Video, Long>
        ): Boolean {
            return oldItem.first.uuid == newItem.first.uuid
        }

    }

    private val diff = AsyncListDiffer(this,diffCallback)

    override fun getItemViewType(position: Int): Int {
        return diff.currentList[position].first.getUsedLayout()
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
            val videoItem:Video = diff.currentList[holder.bindingAdapterPosition].first
            videoViewClick?.onVideoClick(
                videoItem.uuid,
                videoItem.name,
                videoItem.description,
                videoItem.previewPath,
                videoItem.duration
            )
        }


        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val pair = diff.currentList[position]
        val videoItem = pair.first
        when(holder.itemViewType)
        {
            R.layout.list_item_video->{
                val videoHolder = holder as VideoHolder
                videoHolder.apply {
                    Glide.with(itemView.context).load(videoItem.getFullThumbnailPath()).format(DecodeFormat.PREFER_RGB_565).into(banner)
                    videoName.text=videoItem.name
                    videoDuration.text=videoItem.getFormattedDuration()
                    watchedTimeProgressBar.progress = pair.second.toInt() % 100
                }

            }
        }






    }

    override fun getItemCount(): Int {
        return diff.currentList.size
    }

    fun submitList(list:List<Pair<Video,Long>>){
        diff.submitList(list)
    }

    interface VideoViewClick{
        fun onVideoClick(videoId: UUID, videoName: String, videoDescription: String,previewPath:String,duration:Int)
    }




}