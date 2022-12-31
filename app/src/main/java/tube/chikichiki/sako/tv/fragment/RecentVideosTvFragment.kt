package tube.chikichiki.sako.tv.fragment

import android.os.Bundle
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.ViewModelProvider
import tube.chikichiki.sako.Utils
import tube.chikichiki.sako.api.ChikiFetcher
import tube.chikichiki.sako.database.ChikiChikiDatabaseRepository
import tube.chikichiki.sako.model.Video
import tube.chikichiki.sako.model.VideoAndWatchedTimeModel
import tube.chikichiki.sako.tv.activity.TVVideoPlayerActivity
import tube.chikichiki.sako.tv.presenter.VideoTvPresenter
import tube.chikichiki.sako.viewModel.RecentVideosViewModel

class RecentVideosTvFragment: VerticalGridSupportFragment(),
    BrowseSupportFragment.MainFragmentAdapterProvider, OnItemViewClickedListener,
    OnItemViewSelectedListener {

    private lateinit var mGridAdapter: ArrayObjectAdapter
    private val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_MEDIUM
    private var recentVideosViewModel: RecentVideosViewModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recentVideosViewModel= activity?.let { ViewModelProvider(it).get(RecentVideosViewModel::class.java) }

        setupUi()

    }

    private fun setupUi(){

        val gridPresenter = VerticalGridPresenter(ZOOM_FACTOR)
        gridPresenter.numberOfColumns = 3
        this.onItemViewClickedListener = this
        this.setOnItemViewSelectedListener(this)
        setGridPresenter(gridPresenter)

        mGridAdapter = ArrayObjectAdapter(VideoTvPresenter())
        adapter =mGridAdapter

        prepareEntranceTransition()


        loadAndShowRecentVideos()


    }

    private fun loadAndShowRecentVideos(){

        recentVideosViewModel?.recentVideosLiveData?.observe(this){ videos ->

            ChikiChikiDatabaseRepository.get().getAllWatchedVideos().observe(this){

                mGridAdapter.addAll(mGridAdapter.size(),Utils.getPairOfVideos(videos,it))
                startEntranceTransition()

            }




        }

    }



    override fun getMainFragmentAdapter(): BrowseSupportFragment.MainFragmentAdapter<*> {
        return BrowseSupportFragment.MainFragmentAdapter(this)
    }

    override fun onItemClicked(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {
        progressBarManager.show()
        val videoItem = item as VideoAndWatchedTimeModel
        ChikiFetcher().fetchStreamingPlaylist(videoItem.video.uuid).observe(this){
            progressBarManager.hide()
            val intent = TVVideoPlayerActivity.newInstance(activity,videoItem.video.uuid.toString(),videoItem.video.name,videoItem.video.description,videoItem.video.previewPath,videoItem.video.duration)
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