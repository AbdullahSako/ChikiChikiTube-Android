package tube.chikichiki.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import tube.chikichiki.api.ChikiFetcher
import tube.chikichiki.model.Video

class RecentVideosViewModel:ViewModel() {
    val recentVideosLiveData: LiveData<List<Video>>

    init {
        recentVideosLiveData = ChikiFetcher().fetchSortedVideos("-publishedAt")
    }
}