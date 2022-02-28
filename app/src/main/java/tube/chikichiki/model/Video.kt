package tube.chikichiki.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class Video(
    var uuid: UUID,
    var name: String,
    var previewPath: String,
    var description: String,
    @SerializedName("channel") var videoChannel: VideoChannel,
    var duration: Int
) {

    fun getFullThumbnailPath(): String {
        return "https://vtr.chikichiki.tube$previewPath"
    }

    fun getFormattedDuration(): String {
        if (duration < 3600) {
            val seconds = duration % 60
            val minutes = (duration % 3600) / 60

            return String.format("%02d:%02d", minutes, seconds)
        } else {
            val seconds = duration % 60
            val minutes = (duration % 3600) / 60
            val hours = duration / 3600
            return String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }

    }
}