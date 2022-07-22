package tube.chikichiki.sako.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tube.chikichiki.sako.model.Comment
import tube.chikichiki.sako.model.CommentCount
import tube.chikichiki.sako.model.VideoChannel
import java.util.*

class ChikiCommentsFetcher {
    private val chikiCommentApi:ChikiCommentsApi

    init {
        val retrofit: Retrofit = Retrofit.Builder().baseUrl("https://chikichikicommentapi.herokuapp.com/comment/").addConverterFactory(
            GsonConverterFactory.create()
        ).build()
        chikiCommentApi=retrofit.create(ChikiCommentsApi::class.java)
    }


    fun fetchCommentsOfAVideo(videoId:UUID,start:Int=0,count:Int=25): LiveData<List<Comment>> {
        val responseData: MutableLiveData<List<Comment>> = MutableLiveData()
        val request: Call<CommentResponse> =chikiCommentApi.getCommentsOfAVideo(videoId.toString(),count,start)
        request.enqueue(object : Callback<CommentResponse> {
            override fun onResponse(call: Call<CommentResponse>, response: Response<CommentResponse>) {
                Log.d("TESTLOG","Comments RECIEVED")

                val videoCommentData:CommentResponse?=response.body()
                val videoCommentItems: List<Comment> =videoCommentData?.data?: mutableListOf()
                responseData.value=videoCommentItems
            }

            override fun onFailure(call: Call<CommentResponse>, t: Throwable) {
                Log.d("TESTLOG","FAILED TO FETCH Comments")
            }

        })
        return responseData
    }

    fun fetchCommentsCount(videoId:UUID): LiveData<List<CommentCount>> {
        val responseData: MutableLiveData<List<CommentCount>> = MutableLiveData()
        val request: Call<CommentCountResponse> =chikiCommentApi.getCommentCountOfAVideo(videoId.toString())
        request.enqueue(object : Callback<CommentCountResponse> {
            override fun onResponse(call: Call<CommentCountResponse>, response: Response<CommentCountResponse>) {
                Log.d("TESTLOG","Comments counts RECIEVED")

                val videoCommentData:CommentCountResponse?=response.body()
                val videoCommentItems: List<CommentCount> =videoCommentData?.data?: mutableListOf()
                responseData.value=videoCommentItems
            }

            override fun onFailure(call: Call<CommentCountResponse>, t: Throwable) {
                Log.d("TESTLOG","FAILED TO FETCH Comments count")
            }

        })
        return responseData
    }

    fun postLike(commentId:Int) : LiveData<Boolean>{
        val request: Call<String> =chikiCommentApi.postLike(commentId)
        val success:MutableLiveData<Boolean> = MutableLiveData()
        request.enqueue(object :Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d("TESTLOG", "$response like Added")
                success.value = true
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("TESTLOG","FAILED TO add like ${t.message}")
                success.value = false
            }

        })

        return success
    }

    fun postDislike(commentId:Int) : LiveData<Boolean>{
        val request: Call<String> =chikiCommentApi.postDislike(commentId)
        val success:MutableLiveData<Boolean> = MutableLiveData()
        request.enqueue(object :Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d("TESTLOG", "$response dislike Added")
                success.value = true
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("TESTLOG","FAILED TO add dislike ${t.message}")
                success.value = false
            }

        })

        return success
    }

    fun removeLike(commentId:Int) : LiveData<Boolean>{
        val request: Call<String> =chikiCommentApi.removeLike(commentId)
        val success:MutableLiveData<Boolean> = MutableLiveData()
        request.enqueue(object :Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d("TESTLOG", "$response like removed")
                success.value = true
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("TESTLOG","FAILED TO remove like ${t.message}")
                success.value = false
            }

        })

        return success
    }

    fun removeDislike(commentId:Int) : LiveData<Boolean>{
        val request: Call<String> =chikiCommentApi.removeDislike(commentId)
        val success:MutableLiveData<Boolean> = MutableLiveData()
        request.enqueue(object :Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d("TESTLOG", "$response dislike removed")
                success.value = true
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("TESTLOG","FAILED TO remove dislike ${t.message}")
                success.value = false
            }

        })

        return success
    }

    fun addAComment(videoId:UUID,nickname:String,comment:String) : LiveData<Boolean>{
        val request: Call<String> =chikiCommentApi.postComment(videoId.toString(),nickname,comment)
        val success:MutableLiveData<Boolean> = MutableLiveData()
        

        request.enqueue(object :Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d("TESTLOG", "$response Comment Added")
                Log.d("TESTLOG",call.request().toString())
                success.value = response.code() != 500

            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("TESTLOG","FAILED TO add comment ${t.message}")
                success.value = false
            }

        })

        return success
    }



}