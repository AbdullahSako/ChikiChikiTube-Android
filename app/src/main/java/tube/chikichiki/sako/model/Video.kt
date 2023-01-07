package tube.chikichiki.sako.model


import com.google.gson.annotations.SerializedName
import tube.chikichiki.sako.R
import java.util.*

data class Video(
    var uuid: UUID,
    var name: String,
    var previewPath: String,
    var description: String,
    @SerializedName("channel") var videoChannel: VideoChannel,
    var duration: Int,
    var language: Language,
    val loading:Int=0

    ) {

    fun getFullThumbnailPath(): String {
        return "https://vtr.chikichiki.tube$previewPath"
    }

    fun getFormattedDuration(): String {
        return if (duration < 3600) {
            val seconds = duration % 60
            val minutes = (duration % 3600) / 60

            String.format("%02d:%02d", minutes, seconds)
        } else {
            val seconds = duration % 60
            val minutes = (duration % 3600) / 60
            val hours = duration / 3600
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }

    }

    fun getUsedLayout():Int{
        return when (loading) {
            0 -> {
                R.layout.list_item_video
            }
            1 -> {
                R.layout.list_item_loader
            }
            else -> {
                R.layout.list_item_end_of_videos
            }
        }

    }
}