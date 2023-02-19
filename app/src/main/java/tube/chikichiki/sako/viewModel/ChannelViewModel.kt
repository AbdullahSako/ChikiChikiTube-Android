package tube.chikichiki.sako.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import tube.chikichiki.sako.api.ChikiFetcher
import tube.chikichiki.sako.model.VideoChannel

class ChannelViewModel : ViewModel() {
    val channelItemLiveData: LiveData<List<VideoChannel>> = ChikiFetcher().fetchChannels()

}