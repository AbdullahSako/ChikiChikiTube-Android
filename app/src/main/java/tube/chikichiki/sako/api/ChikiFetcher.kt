package tube.chikichiki.sako.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tube.chikichiki.sako.model.*
import java.util.*

class ChikiFetcher {
    private val chikiApi:ChikiChikiApi

    init {
        val retrofit:Retrofit=Retrofit.Builder().baseUrl("https://vtr.chikichiki.tube/api/v1/").addConverterFactory(
            GsonConverterFactory.create()
        ).build()
        chikiApi=retrofit.create(ChikiChikiApi::class.java)
    }

    fun fetchChannels():LiveData<List<VideoChannel>>{
        val responseData:MutableLiveData<List<VideoChannel>> = MutableLiveData()
        val request: Call<VideoChannelDataResponse> =chikiApi.getChannels("-createdAt",50)

        request.enqueue(object :Callback<VideoChannelDataResponse>{
            override fun onResponse(call: Call<VideoChannelDataResponse>, response: Response<VideoChannelDataResponse>) {
                Log.d("TESTLOG","CHANNELS RECIEVED")

                val videoChannelData:VideoChannelDataResponse?=response.body()
                val videoChannelItems: List<VideoChannel> =videoChannelData?.videoChannelItems?: mutableListOf()
                responseData.value=videoChannelItems
            }

            override fun onFailure(call: Call<VideoChannelDataResponse>, t: Throwable) {
                Log.d("TESTLOG","FAILED TO FETCH CHANNELS")
            }

        })
        return responseData
    }

    fun fetchPlaylists(startNumber: Int=0):LiveData<List<VideoPlaylist>>{
        val responseData:MutableLiveData<List<VideoPlaylist>> = MutableLiveData()
        val request: Call<VideoPlaylistResponse> =chikiApi.getPlaylists(100,startNumber)

        request.enqueue(object :Callback<VideoPlaylistResponse>{
            override fun onResponse(call: Call<VideoPlaylistResponse>, response: Response<VideoPlaylistResponse>) {
                Log.d("TESTLOG","Playlists RECIEVED")

                val videoPlaylistResponse:VideoPlaylistResponse?=response.body()
                val videoPlaylistItems:List<VideoPlaylist> =videoPlaylistResponse?.playlistItems?: mutableListOf()
                responseData.value=videoPlaylistItems
            }

            override fun onFailure(call: Call<VideoPlaylistResponse>, t: Throwable) {
                Log.d("TESTLOG","FAILED TO FETCH playlists")
            }

        })
        return responseData
    }

    fun fetchSortedVideos(sort:String):LiveData<List<Video>>{
        val responseData:MutableLiveData<List<Video>> = MutableLiveData()
        val request: Call<VideoResponse> =chikiApi.getSortedVideos(sort)

        request.enqueue(object :Callback<VideoResponse>{
            override fun onResponse(call: Call<VideoResponse>, response: Response<VideoResponse>) {
                Log.d("TESTLOG","Sorted Videos RECIEVED")
                val videoResponse:VideoResponse?=response.body()
                val videoItems:List<Video> =videoResponse?.videoItems?: mutableListOf()
                responseData.value=videoItems

            }

            override fun onFailure(call: Call<VideoResponse>, t: Throwable) {
                Log.d("TESTLOG","FAILED TO FETCH sorted Videos")
            }

        })
        return responseData
    }

    fun fetchVideosOfaChannel(channelHandle:String,startNumber:Int=0,sortBy:String="-createdAt"):LiveData<List<Video>>{
        val responseData:MutableLiveData<List<Video>> = MutableLiveData()
        val request: Call<VideoResponse> =chikiApi.getVideosOfaChannel(channelHandle,100,startNumber,sortBy)

        request.enqueue(object :Callback<VideoResponse>{
            override fun onResponse(call: Call<VideoResponse>, response: Response<VideoResponse>) {
                Log.d("TESTLOG","channel Videos RECIEVED")
                val videoResponse:VideoResponse?=response.body()
                val videoItems:List<Video> =videoResponse?.videoItems?: mutableListOf()
                responseData.value=videoItems
            }

            override fun onFailure(call: Call<VideoResponse>, t: Throwable) {
                Log.d("TESTLOG","FAILED TO FETCH channel Videos")
            }

        })
        return responseData

    }

    fun fetchVideoFile(videoId:UUID):LiveData<List<File>>{
        val responseData:MutableLiveData<List<File>> = MutableLiveData()
        val request:Call<StreamingPlaylistResponse> = chikiApi.getVideo(videoId)

        request.enqueue(object :Callback<StreamingPlaylistResponse>{
            override fun onResponse(
                call: Call<StreamingPlaylistResponse>,
                response: Response<StreamingPlaylistResponse>
            ) {
                Log.d("TESTLOG","video files RECIEVED")
                val streamingPlaylistResponse:StreamingPlaylistResponse?=response.body()
                val streamingPlaylistItems:List<VideoFileResponse> =streamingPlaylistResponse?.streamingPlaylistItems?: mutableListOf()

                responseData.value=streamingPlaylistItems[0].fileItems
            }

            override fun onFailure(call: Call<StreamingPlaylistResponse>, t: Throwable) {
                Log.d("TESTLOG","FAILED TO FETCH video files")
            }

        })
        return responseData

    }

    fun fetchStreamingPlaylist(videoId:UUID):LiveData<List<VideoFileResponse>>{
        val responseData:MutableLiveData<List<VideoFileResponse>> = MutableLiveData()
        val request:Call<StreamingPlaylistResponse> = chikiApi.getVideo(videoId)

        request.enqueue(object :Callback<StreamingPlaylistResponse>{
            override fun onResponse(
                call: Call<StreamingPlaylistResponse>,
                response: Response<StreamingPlaylistResponse>
            ) {
                Log.d("TESTLOG","video files RECIEVED")
                val streamingPlaylistResponse:StreamingPlaylistResponse?=response.body()
                val streamingPlaylistItems:List<VideoFileResponse> =streamingPlaylistResponse?.streamingPlaylistItems?: mutableListOf()
                streamingPlaylistItems[0].apply {
                    publishedAt=streamingPlaylistResponse?.publishedAt
                    views=streamingPlaylistResponse?.views
                }
                responseData.value=streamingPlaylistItems
            }

            override fun onFailure(call: Call<StreamingPlaylistResponse>, t: Throwable) {
                Log.d("TESTLOG","FAILED TO FETCH video files")
            }

        })
        return responseData

    }

    fun searchForVideos(searchTerm:String):LiveData<List<Video>>{
        val responseData:MutableLiveData<List<Video>> = MutableLiveData()
        val request:Call<VideoResponse> = chikiApi.searchVideos(searchTerm,100)

        request.enqueue(object :Callback<VideoResponse>{
            override fun onResponse(call: Call<VideoResponse>, response: Response<VideoResponse>) {
                Log.d("TESTLOG","search videos RECIEVED")
                val videoResponse:VideoResponse?=response.body()
                val videoItems:List<Video> =videoResponse?.videoItems?: mutableListOf()
                responseData.value=videoItems
            }

            override fun onFailure(call: Call<VideoResponse>, t: Throwable) {
                Log.d("TESTLOG","FAILED TO FETCH search videos")
            }

        })
        return responseData
    }

    fun fetchVideosOfaPlaylist(playlistId:Int,startNumber:Int=0):LiveData<List<Video>>{
        val responseData:MutableLiveData<List<Video>> = MutableLiveData()
        val request: Call<PlaylistVideoResponse> =chikiApi.getVideosOfAPlaylist(playlistId,100,startNumber)

        request.enqueue(object :Callback<PlaylistVideoResponse>{
            override fun onResponse(call: Call<PlaylistVideoResponse>, response: Response<PlaylistVideoResponse>) {
                Log.d("TESTLOG","playlist Videos RECIEVED")
                val videoResponse:PlaylistVideoResponse?=response.body()
                val videoItems:List<Video>? =videoResponse?.getVideos()
                responseData.value=videoItems
            }

            override fun onFailure(call: Call<PlaylistVideoResponse>, t: Throwable) {
                Log.d("TESTLOG","FAILED TO FETCH playlist Videos")
            }

        })
        return responseData

    }

    fun fetchCaptions(videoId: UUID):LiveData<List<Caption>>{
        val responseData:MutableLiveData<List<Caption>> = MutableLiveData()
        val request: Call<CaptionResponse> =chikiApi.getVideoCaption(videoId)

        request.enqueue(object :Callback<CaptionResponse>{
            override fun onResponse(call: Call<CaptionResponse>, response: Response<CaptionResponse>) {
                Log.d("TESTLOG","video Captions RECIEVED")

                val captionResponse:CaptionResponse?=response.body()
                val captionItems:List<Caption>? =captionResponse?.captionList
                responseData.value=captionItems
            }

            override fun onFailure(call: Call<CaptionResponse>, t: Throwable) {
                Log.d("TESTLOG","FAILED TO FETCH video Captions")
            }

        })

        return responseData

    }

    fun addAView(videoId:UUID,currentTimeSec:Int?){
        val request: Call<String> =chikiApi.addView(videoId,currentTimeSec?:0)

        request.enqueue(object :Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d("TESTLOG", "$response View Added")
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("TESTLOG","FAILED TO add view")
            }

        })
    }

}