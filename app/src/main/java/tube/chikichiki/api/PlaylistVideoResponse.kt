package tube.chikichiki.api

import com.google.gson.annotations.SerializedName
import tube.chikichiki.model.PlayListVideo
import tube.chikichiki.model.Video

class PlaylistVideoResponse {
    @SerializedName("data")
    private lateinit var playlistVideoItems:List<PlayListVideo>

    fun getVideos():List<Video>{
        val tempList:MutableList<Video> = mutableListOf()
        playlistVideoItems.forEach { tempList.add(it.video) }
        return tempList
    }
}