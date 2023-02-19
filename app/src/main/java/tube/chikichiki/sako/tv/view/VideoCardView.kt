package tube.chikichiki.sako.tv.view

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.leanback.widget.BaseCardView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import tube.chikichiki.sako.R
import tube.chikichiki.sako.model.HistoryVideoInfo
import tube.chikichiki.sako.model.VideoAndWatchedTimeModel
import tube.chikichiki.sako.model.WatchLater

class VideoCardView(context: Context) : BaseCardView(context) {

    init {
        LayoutInflater.from(context).inflate(R.layout.list_item_video_tv, this)
        setOnFocusChangeListener { view, b -> }
        //isFocusable = true
    }

    fun updateUi(item: VideoAndWatchedTimeModel) {
        val titleTextView: TextView = findViewById(R.id.tv_playlist_title)
        val imageView: ImageView = findViewById(R.id.tv_video_image)
        val duration: TextView = findViewById(R.id.tv_playlist_no_of_videos)
        val watchedTime: ProgressBar = findViewById(R.id.tv_watched_time_progress_bar)

        titleTextView.text = item.video.name
        duration.text = item.video.getFormattedDuration()
        watchedTime.progress = item.watchedTime.toInt() % 101

        Glide.with(context).load(item.video.getFullThumbnailPath()).format(
            DecodeFormat.PREFER_RGB_565
        ).into(imageView)
    }

    fun updateUi(watchLater: WatchLater) {

        val titleTextView: TextView = findViewById(R.id.tv_playlist_title)
        val imageView: ImageView = findViewById(R.id.tv_video_image)
        val duration: TextView = findViewById(R.id.tv_playlist_no_of_videos)


        titleTextView.text = watchLater.name
        duration.text = watchLater.getFormattedDuration()

        Glide.with(context).load(watchLater.getFullThumbnailPath()).format(
            DecodeFormat.PREFER_RGB_565
        ).into(imageView)
    }

    fun updateUi(historyItem: HistoryVideoInfo) {
        val titleTextView: TextView = findViewById(R.id.tv_playlist_title)
        val imageView: ImageView = findViewById(R.id.tv_video_image)
        val duration: TextView = findViewById(R.id.tv_playlist_no_of_videos)



        titleTextView.text = historyItem.name
        duration.text = historyItem.getFormattedDuration()

        Glide.with(context).load(historyItem.getFullThumbnailPath()).format(
            DecodeFormat.PREFER_RGB_565
        ).into(imageView)
    }


}