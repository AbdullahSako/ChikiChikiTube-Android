package tube.chikichiki.sako.model

import com.google.gson.annotations.SerializedName

data class VideoChannel(
    var id: Int,
    var displayName: String = "",
    @SerializedName("banner")
    var banner: Banner?,
    var description: String = "",
    @SerializedName("name")
    var channelHandle: String
)