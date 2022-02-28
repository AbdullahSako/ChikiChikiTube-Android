package tube.chikichiki.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class VideoPlaylist(
    var uuid: UUID,
    var displayName: String = "",
    var thumbnailPath: String = "",
    var videoChannel: VideoChannel,
    var description: String,
    @SerializedName("videosLength")
    var numberOfVideos: Int
) {

    fun getFullThumbnailPath(): String {
        return "https://vtr.chikichiki.tube$thumbnailPath"
    }
}