package tube.chikichiki.sako.tv.fragment

import android.os.Bundle
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.BrowseSupportFragment.MainFragmentAdapter
import androidx.leanback.app.BrowseSupportFragment.MainFragmentAdapterProvider
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.VerticalGridPresenter
import tube.chikichiki.sako.api.ChikiFetcher
import tube.chikichiki.sako.tv.presenter.VideoTvPresenter

class MostViewedVideosTvFragment: VerticalGridSupportFragment(),MainFragmentAdapterProvider {
    private lateinit var mGridAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupUi()
        prepareEntranceTransition()
        loadAndShowMostViewedVideos()

    }

    private fun setupUi(){

        val gridPresenter = VerticalGridPresenter()
        gridPresenter.numberOfColumns = 4
        setGridPresenter(gridPresenter)

        mGridAdapter = ArrayObjectAdapter(gridPresenter)
        this.adapter = mGridAdapter
    }

    private fun loadAndShowMostViewedVideos(){
        val videoPresenter = VideoTvPresenter()

        ChikiFetcher().fetchSortedVideos("-views").observe(this){ videos ->
            videos.forEach {
                val test= ArrayObjectAdapter(videoPresenter)
                test.add(it)
                mGridAdapter.add(test)


            }

        }


    }

    override fun getMainFragmentAdapter(): BrowseSupportFragment.MainFragmentAdapter<*> {
        return MainFragmentAdapter(this)
    }


}

