package tube.chikichiki.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import tube.chikichiki.api.ChikiFetcher
import tube.chikichiki.model.VideoChannel
import tube.chikichiki.model.VideoPlaylist

class playListViewModel : ViewModel() {
    val playListItemLiveData: LiveData<List<VideoPlaylist>>

    init {
        playListItemLiveData= ChikiFetcher().fetchPlaylists()
    }
}