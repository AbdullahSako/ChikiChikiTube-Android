package tube.chikichiki.sako.api

import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface ChikiChikiApi {
    @GET("video-channels")
    fun getChannels(@Query("sort")sort:String,@Query("count")numberOfChannels:Int): Call<VideoChannelDataResponse>

    @GET("video-playlists")
    fun getPlaylists(@Query("count")count:Int,@Query("start")startNumber: Int): Call<VideoPlaylistResponse>

    @GET("videos")
    fun getSortedVideos(@Query("sort")sort:String):Call<VideoResponse>

    @GET("video-channels/{channelHandle}/videos")
    fun getVideosOfaChannel(@Path("channelHandle") channelHandle:String,@Query("count")count:Int,@Query("start")startNumber:Int,@Query("sort")sortBy:String):Call<VideoResponse>

    @GET("videos/{id}")
    fun getVideo(@Path("id") videoId:UUID):Call<StreamingPlaylistResponse>

    @GET("search/videos")
    fun searchVideos(@Query("search")searchTerm:String,@Query("count") numberOfVideos:Int):Call<VideoResponse>

    @GET("video-playlists/{playlistId}/videos")
    fun getVideosOfAPlaylist(@Path("playlistId")playlistId:Int,@Query("count") count: Int,@Query("start")startNumber: Int):Call<PlaylistVideoResponse>

    @GET("videos/{videoId}/captions")
    fun getVideoCaption(@Path("videoId") videoId: UUID) :Call<CaptionResponse>

    @POST("videos/{id}/views")
    fun addView(@Path("id")videoId: UUID , @Query("currentTime")currentTime:Int):Call<String>
}