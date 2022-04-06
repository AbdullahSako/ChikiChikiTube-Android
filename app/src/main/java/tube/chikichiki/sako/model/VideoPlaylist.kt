package tube.chikichiki.sako.model

import com.google.gson.annotations.SerializedName

data class VideoPlaylist(
    var id: Int,
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