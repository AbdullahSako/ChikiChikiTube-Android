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
import tube.chikichiki.sako.model.VideoPlaylist

class PlaylistAdapter(private val playlistItems:List<VideoPlaylist>):RecyclerView.Adapter<PlaylistAdapter.PlaylistHolder>() {

    private var playlistViewClick:PlaylistViewClick?=null

    inner class PlaylistHolder(view: View):RecyclerView.ViewHolder(view){
        val banner: ImageView = itemView.findViewById(R.id.playlist_banner)
        val playlistName: TextView = itemView.findViewById(R.id.playlist_name)
        val playlistVideoSize:TextView=itemView.findViewById(R.id.playlist_video_size)
    }

    fun setPlaylistViewClickListener(clickListener:PlaylistViewClick){
        playlistViewClick=clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_playlist,parent,false)
        return PlaylistHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistHolder, position: Int) {
        val playlistItem = playlistItems[position]
        holder.apply {
            Glide.with(itemView.context).load(playlistItem.getFullThumbnailPath()).format(
                DecodeFormat.PREFER_RGB_565).into(banner)
            playlistName.text=playlistItem.displayName
            playlistVideoSize.text=playlistItem.numberOfVideos.toString()
            itemView.setOnClickListener {
                playlistViewClick?.onPlayListClick(itemView,playlistItem.id)
            }
        }
    }

    override fun getItemCount(): Int {
        return playlistItems.size
    }

    interface PlaylistViewClick{
        fun onPlayListClick(view:View,playlistId:Int)
    }

}