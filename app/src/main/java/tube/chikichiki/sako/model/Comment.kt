package tube.chikichiki.sako.model


import com.google.gson.annotations.SerializedName

data class Comment(
    @SerializedName("comment")
    val comment: String,
    @SerializedName("Commentlike")
    val commentlike: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("Dislike")
    val dislike: Int,
    @SerializedName("Id")
    val id: Int,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("video_id")
    val videoId: String
)