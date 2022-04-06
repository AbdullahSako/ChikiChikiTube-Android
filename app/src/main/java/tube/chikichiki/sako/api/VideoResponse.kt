package tube.chikichiki.sako.api

import com.google.gson.annotations.SerializedName
import tube.chikichiki.sako.model.Video

class VideoResponse {
    @SerializedName("data")
    lateinit var videoItems:List<Video>

    @SerializedName("total")
    var total:Int=0
}