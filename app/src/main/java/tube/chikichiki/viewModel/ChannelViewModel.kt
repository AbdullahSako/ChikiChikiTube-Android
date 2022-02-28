package tube.chikichiki.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import tube.chikichiki.api.ChikiFetcher
import tube.chikichiki.model.VideoChannel

class ChannelViewModel :ViewModel() {
    val channelItemLiveData: LiveData<List<VideoChannel>>

    init {
        channelItemLiveData= ChikiFetcher().fetchChannels()
    }
}