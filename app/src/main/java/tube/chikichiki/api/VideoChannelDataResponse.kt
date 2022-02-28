package tube.chikichiki.api

import com.google.gson.annotations.SerializedName
import tube.chikichiki.model.VideoChannel

class VideoChannelDataResponse {
    @SerializedName("data")
    lateinit var videoChannelItems:List<VideoChannel>
}