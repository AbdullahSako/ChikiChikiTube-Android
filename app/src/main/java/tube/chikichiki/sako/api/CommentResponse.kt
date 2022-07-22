package tube.chikichiki.sako.api


import com.google.gson.annotations.SerializedName
import tube.chikichiki.sako.model.Comment

data class CommentResponse(
    @SerializedName("data")
    val `data`: List<Comment>
)