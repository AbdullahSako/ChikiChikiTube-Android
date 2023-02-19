package tube.chikichiki.sako.tv.view

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.leanback.widget.BaseCardView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import tube.chikichiki.sako.R
import tube.chikichiki.sako.model.VideoPlaylist

class PlaylistCardView(context: Context) : BaseCardView(context) {

    init {
        LayoutInflater.from(context).inflate(R.layout.list_item_playlist_tv, this)
        setOnFocusChangeListener { view, b -> }
        //isFocusable = true
    }

    fun updateUi(playlist: VideoPlaylist) {
        val titleTextView: TextView = findViewById(R.id.playlist_tv_name)
        val imageView: ImageView = findViewById(R.id.playlist_tv_banner)
        val size: TextView = findViewById(R.id.playlist_video_tv_size)

        titleTextView.text = playlist.displayName
        size.text = playlist.numberOfVideos.toString() + " " + resources.getString(R.string.videos)

        Glide.with(context).load(playlist.getFullThumbnailPath()).format(
            DecodeFormat.PREFER_RGB_565
        ).into(imageView)
    }

}