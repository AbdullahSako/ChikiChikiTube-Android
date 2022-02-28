package tube.chikichiki.api

import com.google.gson.annotations.SerializedName
import tube.chikichiki.model.VideoPlaylist

class VideoPlaylistResponse {
 @SerializedName("data")
 lateinit var playlistItems:List<VideoPlaylist>
}