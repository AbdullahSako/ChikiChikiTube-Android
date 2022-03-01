package tube.chikichiki.api

import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface ChikiChikiApi {
    @GET("video-channels")
    fun getChannels(@Query("sort")sort:String): Call<VideoChannelDataResponse>

    @GET("video-playlists")
    fun getPlaylists(@Query("count")count:Int): Call<VideoPlaylistResponse>

    @GET("videos")
    fun getSortedVideos(@Query("sort")sort:String):Call<VideoResponse>

    @GET("video-channels/{channelHandle}/videos")
    fun getVideosOfaChannel(@Path("channelHandle") channelHandle:String,@Query("count")count:Int,@Query("start")startNumber:Int,@Query("sort")sortBy:String):Call<VideoResponse>

    @GET("videos/{id}")
    fun getVideo(@Path("id") videoId:UUID):Call<StreamingPlaylistResponse>
}