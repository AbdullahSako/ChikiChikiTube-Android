package tube.chikichiki.sako.tv.view

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.leanback.widget.BaseCardView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import tube.chikichiki.sako.R
import tube.chikichiki.sako.model.Video
import tube.chikichiki.sako.model.WatchLater

class VideoCardView(context:Context):BaseCardView(context) {

    init {
        LayoutInflater.from(context).inflate(R.layout.list_item_video_tv,this)
        setOnFocusChangeListener { view, b ->  }
        //isFocusable = true
    }

    fun updateUi(video:Video){
        val titleTextView:TextView =findViewById(R.id.tv_video_title)
        val imageView:ImageView = findViewById(R.id.tv_video_image)
        val duration:TextView = findViewById(R.id.tv_video_duration)

        titleTextView.text = video.name
        duration.text = video.getFormattedDuration()

        Glide.with(context).load(video.getFullThumbnailPath()).format(
            DecodeFormat.PREFER_RGB_565).into(imageView)
    }

    fun updateUi(watchLater: WatchLater){

        val titleTextView:TextView =findViewById(R.id.tv_video_title)
        val imageView:ImageView = findViewById(R.id.tv_video_image)
        val duration:TextView = findViewById(R.id.tv_video_duration)

        titleTextView.text = watchLater.name
        duration.text = watchLater.getFormattedDuration()

        Glide.with(context).load(watchLater.getFullThumbnailPath()).format(
            DecodeFormat.PREFER_RGB_565).into(imageView)
    }


}