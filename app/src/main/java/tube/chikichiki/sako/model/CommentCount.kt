package tube.chikichiki.sako.model


import com.google.gson.annotations.SerializedName

data class CommentCount(
    @SerializedName("count")
    val count: Int
)