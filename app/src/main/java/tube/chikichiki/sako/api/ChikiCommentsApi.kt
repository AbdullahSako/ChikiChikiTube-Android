package tube.chikichiki.sako.api

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ChikiCommentsApi {

    @GET(" ")
    fun getCommentsOfAVideo(@Query("video_id")videoId:String, @Query("count")count:Int,@Query("start")start:Int): Call<CommentResponse>

    @GET("count")
    fun getCommentCountOfAVideo(@Query("video_id")videoId:String): Call<CommentCountResponse>

    @POST(" ")
    fun postComment(@Query("video_id")videoId:String,@Query("nickname") nickname:String,@Query("comment")comment:String) :Call<String>

    @POST("like")
    fun postLike(@Query("id")commentId:Int) :Call<String>

    @POST("dislike")
    fun postDislike(@Query("id")commentId:Int) :Call<String>

    @DELETE("like")
    fun removeLike(@Query("id")commentId:Int):Call<String>

    @DELETE("dislike")
    fun removeDislike(@Query("id")commentId:Int):Call<String>
}