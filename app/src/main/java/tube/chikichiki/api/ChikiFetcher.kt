package tube.chikichiki.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import tube.chikichiki.model.Video
import tube.chikichiki.model.VideoChannel
import tube.chikichiki.model.VideoPlaylist

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
        val request: Call<VideoChannelDataResponse> =chikiApi.getChannels("-createdAt")

        request.enqueue(object :Callback<VideoChannelDataResponse>{
            override fun onResponse(call: Call<VideoChannelDataResponse>, response: Response<VideoChannelDataResponse>) {
                Log.d("TESTLOG","CHANNELS RECIEVED")

                val videoChannelData:VideoChannelDataResponse?=response.body()
                var videoChannelItems: List<VideoChannel> =videoChannelData?.videoChannelItems?: mutableListOf()
                responseData.value=videoChannelItems
            }

            override fun onFailure(call: Call<VideoChannelDataResponse>, t: Throwable) {
                Log.d("TESTLOG","FAILED TO FETCH CHANNELS")
            }

        })
        return responseData
    }

    fun fetchPlaylists():LiveData<List<VideoPlaylist>>{
        val responseData:MutableLiveData<List<VideoPlaylist>> = MutableLiveData()
        val request: Call<VideoPlaylistResponse> =chikiApi.getPlaylists(93)

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
        val request: Call<VideoResponse> =chikiApi.getVideosOfaChannel(channelHandle,50,startNumber,sortBy)

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


}