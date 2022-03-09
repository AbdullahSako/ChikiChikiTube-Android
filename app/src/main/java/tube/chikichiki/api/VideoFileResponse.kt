package tube.chikichiki.api

import com.google.gson.annotations.SerializedName
import tube.chikichiki.model.File

class VideoFileResponse(val playlistUrl:String) {
    @SerializedName("files")
    lateinit var fileItems:List<File>
    var views:String?=null
    var publishedAt:String?=null


}