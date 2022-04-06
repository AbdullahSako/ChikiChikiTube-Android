package tube.chikichiki.sako.api

import com.google.gson.annotations.SerializedName
import tube.chikichiki.sako.model.File

class VideoFileResponse(val playlistUrl:String) {
    @SerializedName("files")
    lateinit var fileItems:List<File>
    var views:String?=null
    var publishedAt:String?=null


}