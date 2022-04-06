package tube.chikichiki.sako.api

import com.google.gson.annotations.SerializedName

class StreamingPlaylistResponse(val publishedAt:String,val views:String) {
    @SerializedName("streamingPlaylists")
    lateinit var streamingPlaylistItems:List<VideoFileResponse>


}