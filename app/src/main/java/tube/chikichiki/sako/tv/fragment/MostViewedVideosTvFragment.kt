package tube.chikichiki.sako.tv.fragment

import android.os.Bundle
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.BrowseSupportFragment.MainFragmentAdapter
import androidx.leanback.app.BrowseSupportFragment.MainFragmentAdapterProvider
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.ViewModelProvider
import tube.chikichiki.sako.api.ChikiFetcher
import tube.chikichiki.sako.model.Video
import tube.chikichiki.sako.tv.activity.VideoPlayerActivity
import tube.chikichiki.sako.tv.presenter.VideoTvPresenter
import tube.chikichiki.sako.viewModel.MostViewedVideosViewModel

class MostViewedVideosTvFragment: VerticalGridSupportFragment(),MainFragmentAdapterProvider,OnItemViewClickedListener,OnItemViewSelectedListener {
    private lateinit var mGridAdapter: ArrayObjectAdapter
    private val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_MEDIUM
    private var mostViewedVideosViewModel: MostViewedVideosViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mostViewedVideosViewModel = activity?.let { ViewModelProvider(it).get(MostViewedVideosViewModel::class.java) }

        setupUi()

    }

    private fun setupUi(){

        val gridPresenter = VerticalGridPresenter(ZOOM_FACTOR)
        gridPresenter.numberOfColumns = 4
        this.onItemViewClickedListener = this
        this.setOnItemViewSelectedListener(this)
        setGridPresenter(gridPresenter)

        mGridAdapter = ArrayObjectAdapter(VideoTvPresenter())
        adapter =mGridAdapter
        prepareEntranceTransition()


        loadAndShowMostViewedVideos()


    }

    private fun loadAndShowMostViewedVideos(){

        mostViewedVideosViewModel?.mostViewedVideosLiveData?.observe(this){ videos ->
            mGridAdapter.addAll(mGridAdapter.size(),videos)
            startEntranceTransition()


        }

    }

    override fun getMainFragmentAdapter(): BrowseSupportFragment.MainFragmentAdapter<*> {
        return MainFragmentAdapter(this)
    }

    override fun onItemClicked(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {
        progressBarManager.show()
        val video = item as Video
        ChikiFetcher().fetchStreamingPlaylist(video.uuid).observe(this){
            progressBarManager.hide()
            val intent = VideoPlayerActivity.newInstance(activity,video.uuid.toString(),video.name,video.description,video.previewPath,video.duration)
            startActivity(intent)
        }

    }

    override fun onItemSelected(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {

    }


}

