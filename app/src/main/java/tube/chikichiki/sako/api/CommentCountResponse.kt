package tube.chikichiki.sako.api


import com.google.gson.annotations.SerializedName
import tube.chikichiki.sako.model.CommentCount

data class CommentCountResponse(
    @SerializedName("data")
    val `data`: List<CommentCount>
)