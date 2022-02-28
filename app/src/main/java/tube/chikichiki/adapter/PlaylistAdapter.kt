package tube.chikichiki.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tube.chikichiki.R
import tube.chikichiki.model.VideoPlaylist

class PlaylistAdapter(private val playlistItems:List<VideoPlaylist>):RecyclerView.Adapter<PlaylistAdapter.PlaylistHolder>() {

    inner class PlaylistHolder(view: View):RecyclerView.ViewHolder(view){
        val banner: ImageView = itemView.findViewById(R.id.playlist_banner)
        val playlistName: TextView = itemView.findViewById(R.id.playlist_name)
        val playlistVideoSize:TextView=itemView.findViewById(R.id.playlist_video_size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_playlist,parent,false)
        return PlaylistHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistHolder, position: Int) {
        val playlistItem = playlistItems[position]
        holder.apply {
            Glide.with(itemView.context).load(playlistItem.getFullThumbnailPath()).into(banner)
            playlistName.text=playlistItem.displayName
            playlistVideoSize.text=playlistItem.numberOfVideos.toString()
        }
    }

    override fun getItemCount(): Int {
        return playlistItems.size
    }

}