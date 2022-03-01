package tube.chikichiki.api

import com.google.gson.annotations.SerializedName
import tube.chikichiki.model.File

class VideoFileResponse {
    @SerializedName("files")
    lateinit var fileItems:List<File>

    fun getFirst():File{
        return fileItems[0]
    }
}