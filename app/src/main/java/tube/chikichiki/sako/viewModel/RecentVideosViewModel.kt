package tube.chikichiki.sako.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import tube.chikichiki.sako.api.ChikiFetcher
import tube.chikichiki.sako.model.Video

class RecentVideosViewModel : ViewModel() {
    val recentVideosLiveData: LiveData<List<Video>> =
        ChikiFetcher().fetchSortedVideos("-publishedAt")

}