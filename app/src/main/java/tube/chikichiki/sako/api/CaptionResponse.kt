package tube.chikichiki.sako.api

import com.google.gson.annotations.SerializedName
import tube.chikichiki.sako.model.Caption
import tube.chikichiki.sako.model.PlayListVideo

class CaptionResponse {
    @SerializedName("data")
    lateinit var captionList:List<Caption>



}