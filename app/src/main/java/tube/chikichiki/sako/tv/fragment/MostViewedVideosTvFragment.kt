package tube.chikichiki.sako.tv.fragment

import android.os.Bundle
import android.util.Log
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.BrowseSupportFragment.MainFragmentAdapter
import androidx.leanback.app.BrowseSupportFragment.MainFragmentAdapterProvider
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.VerticalGridPresenter
import androidx.leanback.widget.VerticalGridView
import tube.chikichiki.sako.api.ChikiFetcher
import tube.chikichiki.sako.tv.presenter.CardPresenter
import tube.chikichiki.sako.tv.presenter.VideoTvPresenter

class MostViewedVideosTvFragment: VerticalGridSupportFragment(),MainFragmentAdapterProvider {
    private lateinit var mGridAdapter: ArrayObjectAdapter
    private val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_SMALL
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupUi()
        prepareEntranceTransition()
        loadAndShowMostViewedVideos()

    }

    private fun setupUi(){

        val gridPresenter = VerticalGridPresenter(ZOOM_FACTOR)
        gridPresenter.numberOfColumns = 4
        setGridPresenter(gridPresenter)

        mGridAdapter = ArrayObjectAdapter(CardPresenter())
        adapter =mGridAdapter
    }

    private fun loadAndShowMostViewedVideos(){

        ChikiFetcher().fetchSortedVideos("-views").observe(this){ videos ->
            for (i in 0 until 10){
                mGridAdapter.add(i,videos[i % 5])
            }


        }

    }

    override fun getMainFragmentAdapter(): BrowseSupportFragment.MainFragmentAdapter<*> {
        return MainFragmentAdapter(this)
    }


}

