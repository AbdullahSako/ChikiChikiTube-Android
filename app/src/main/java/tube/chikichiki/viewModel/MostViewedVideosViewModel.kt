package tube.chikichiki.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import tube.chikichiki.api.ChikiFetcher
import tube.chikichiki.model.Video

class MostViewedVideosViewModel:ViewModel() {
    val mostViewedVideosLiveData: LiveData<List<Video>> = ChikiFetcher().fetchSortedVideos("-views")


}