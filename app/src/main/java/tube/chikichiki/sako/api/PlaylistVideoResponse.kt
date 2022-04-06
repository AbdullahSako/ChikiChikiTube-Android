package tube.chikichiki.sako.api

import com.google.gson.annotations.SerializedName
import tube.chikichiki.sako.model.PlayListVideo
import tube.chikichiki.sako.model.Video

class PlaylistVideoResponse {
    @SerializedName("data")
    private lateinit var playlistVideoItems:List<PlayListVideo>

    fun getVideos():List<Video>{
        val tempList:MutableList<Video> = mutableListOf()
        playlistVideoItems.forEach { tempList.add(it.video) }
        return tempList
    }
}