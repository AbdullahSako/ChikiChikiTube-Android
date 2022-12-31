package tube.chikichiki.sako.tv.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.widget.*
import tube.chikichiki.sako.Utils
import tube.chikichiki.sako.api.ChikiFetcher
import tube.chikichiki.sako.database.ChikiChikiDatabaseRepository
import tube.chikichiki.sako.model.VideoAndWatchedTimeModel
import tube.chikichiki.sako.tv.activity.TVPlaylistVideosActivity
import tube.chikichiki.sako.tv.activity.TVVideoPlayerActivity
import tube.chikichiki.sako.tv.presenter.ChannelListRowPresenter
import tube.chikichiki.sako.tv.presenter.VideoTvPresenter

class SearchFragment:SearchSupportFragment(),SearchSupportFragment.SearchResultProvider {
    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
    private val cardsAdapter = ArrayObjectAdapter(VideoTvPresenter())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSearchResultProvider(this)
        setupItemClickListener()
        
    }




    private fun setupItemClickListener(){

        setOnItemViewClickedListener { itemViewHolder, item, rowViewHolder, row ->

            val videoItem = item as VideoAndWatchedTimeModel

            val intent = TVVideoPlayerActivity.newInstance(requireActivity(),videoItem.video.uuid.toString(),videoItem.video.name,videoItem.video.description,videoItem.video.previewPath,videoItem.video.duration)
            startActivity(intent)
        }

    }

    override fun getResultsAdapter(): ObjectAdapter {
        return rowsAdapter
    }

    override fun onQueryTextChange(newQuery: String?): Boolean {
        if(!TextUtils.isEmpty(newQuery)){
            if (newQuery != null) {
                ChikiFetcher().searchForVideos(newQuery).observe(this){ videos ->
                    ChikiChikiDatabaseRepository.get().getAllWatchedVideos().observe(this){
                        rowsAdapter.clear()
                        cardsAdapter.addAll(cardsAdapter.size(),Utils.getPairOfVideos(videos,it))

                        val header = HeaderItem(rowsAdapter.size().toLong(), null)
                        rowsAdapter.add(ListRow(header, cardsAdapter))

                    }
                }
            }

        }
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {

        return true
    }


}