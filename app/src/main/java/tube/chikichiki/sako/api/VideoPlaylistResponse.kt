package tube.chikichiki.sako.api

import com.google.gson.annotations.SerializedName
import tube.chikichiki.sako.model.VideoPlaylist

class VideoPlaylistResponse {
 @SerializedName("data")
 lateinit var playlistItems:List<VideoPlaylist>
}