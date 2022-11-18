package tube.chikichiki.sako.tv.fragment

import android.os.Bundle
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.ViewModelProvider
import tube.chikichiki.sako.tv.presenter.VideoTvPresenter
import tube.chikichiki.sako.viewModel.MostViewedVideosViewModel

class HistoryTvFragment: VerticalGridSupportFragment(),
    BrowseSupportFragment.MainFragmentAdapterProvider, OnItemViewClickedListener,
    OnItemViewSelectedListener {
    private lateinit var mGridAdapter: ArrayObjectAdapter
    private val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_MEDIUM
    private var mostViewedVideosViewModel: MostViewedVideosViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mostViewedVideosViewModel =
            activity?.let { ViewModelProvider(it).get(MostViewedVideosViewModel::class.java) }

        setupUi()

    }

    private fun setupUi() {

        val gridPresenter = VerticalGridPresenter(ZOOM_FACTOR)
        gridPresenter.numberOfColumns = 4
        this.onItemViewClickedListener = this
        this.setOnItemViewSelectedListener(this)
        setGridPresenter(gridPresenter)

        mGridAdapter = ArrayObjectAdapter(VideoTvPresenter())
        adapter = mGridAdapter

        prepareEntranceTransition()


        loadAndShowMostViewedVideos()


    }

    private fun loadAndShowMostViewedVideos() {

        mostViewedVideosViewModel?.mostViewedVideosLiveData?.observe(this) { videos ->
            mGridAdapter.addAll(mGridAdapter.size(), videos)
            startEntranceTransition()


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

    }

    override fun onItemSelected(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {

    }
}
