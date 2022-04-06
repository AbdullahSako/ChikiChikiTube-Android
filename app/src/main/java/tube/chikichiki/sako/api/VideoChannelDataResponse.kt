package tube.chikichiki.sako.api

import com.google.gson.annotations.SerializedName
import tube.chikichiki.sako.model.VideoChannel

class VideoChannelDataResponse {
    @SerializedName("data")
    lateinit var videoChannelItems:List<VideoChannel>

}