package tube.chikichiki.api

import com.google.gson.annotations.SerializedName

class StreamingPlaylistResponse {
    @SerializedName("streamingPlaylists")
    lateinit var streamingPlaylistItems:List<VideoFileResponse>
}