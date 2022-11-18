package tube.chikichiki.sako.tv.fragment

import android.os.Bundle
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import tube.chikichiki.sako.database.ChikiChikiDatabaseRepository
import tube.chikichiki.sako.tv.presenter.VideoTvPresenter
import tube.chikichiki.sako.tv.presenter.WatchLaterTvPresenter

class WatchLaterTvFragment: VerticalGridSupportFragment(),
    BrowseSupportFragment.MainFragmentAdapterProvider, OnItemViewClickedListener,
    OnItemViewSelectedListener {
    private lateinit var mGridAdapter: ArrayObjectAdapter
    private val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_MEDIUM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUi()

    }


    private fun setupUi() {

        val gridPresenter = VerticalGridPresenter(ZOOM_FACTOR)
        gridPresenter.numberOfColumns = 4
        this.onItemViewClickedListener = this
        this.setOnItemViewSelectedListener(this)
        setGridPresenter(gridPresenter)

        mGridAdapter = ArrayObjectAdapter(WatchLaterTvPresenter())
        adapter = mGridAdapter

        prepareEntranceTransition()


        loadAndShowMostViewedVideos()


    }

    private fun loadAndShowMostViewedVideos() {

        ChikiChikiDatabaseRepository.get().getAllWatchLater().observe(this){ watchLaterList ->
            mGridAdapter.addAll(mGridAdapter.size(), watchLaterList)
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