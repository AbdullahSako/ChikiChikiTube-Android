package tube.chikichiki.sako.tv.fragment

import tube.chikichiki.sako.R
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.widget.*
import tube.chikichiki.sako.Utils
import tube.chikichiki.sako.api.ChikiFetcher
import tube.chikichiki.sako.database.ChikiChikiDatabaseRepository
import tube.chikichiki.sako.model.VideoAndWatchedTimeModel
import tube.chikichiki.sako.tv.activity.TVVideoPlayerActivity
import tube.chikichiki.sako.tv.presenter.VideoTvPresenter


class SearchFragment : SearchSupportFragment(), SearchSupportFragment.SearchResultProvider {
    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
    private val cardsAdapter = ArrayObjectAdapter(VideoTvPresenter())
    private lateinit var grainAnimation: AnimationDrawable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSearchResultProvider(this)
        setupItemClickListener()

    }

    //hide microphone
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = super.onCreateView(inflater, container, savedInstanceState)
        val searchFrame: FrameLayout? = root?.findViewById(androidx.leanback.R.id.lb_search_frame)
        val mSearchBar = searchFrame?.findViewById<SearchBar>(androidx.leanback.R.id.lb_search_bar)
        val mSpeechOrbView = mSearchBar?.findViewById<SpeechOrbView>(androidx.leanback.R.id.lb_search_bar_speech_orb)


        if (mSpeechOrbView != null) {
            mSpeechOrbView.visibility = View.GONE
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBackgroundAnimation()
    }


    private fun setupItemClickListener() {

        setOnItemViewClickedListener { itemViewHolder, item, rowViewHolder, row ->

            val videoItem = item as VideoAndWatchedTimeModel

            val intent = TVVideoPlayerActivity.newInstance(
                requireActivity(),
                videoItem.video.uuid.toString(),
                videoItem.video.name,
                videoItem.video.description,
                videoItem.video.previewPath,
                videoItem.video.duration
            )
            startActivity(intent)
        }

    }

    override fun getResultsAdapter(): ObjectAdapter {
        return rowsAdapter
    }

    override fun onQueryTextChange(newQuery: String?): Boolean {
        if (!TextUtils.isEmpty(newQuery)) {
            if (newQuery != null) {
                ChikiFetcher().searchForVideos(newQuery).observe(this) { videos ->
                    ChikiChikiDatabaseRepository.get().getAllWatchedVideos().observe(this) {
                        rowsAdapter.clear()
                        cardsAdapter.clear()
                        cardsAdapter.addAll(cardsAdapter.size(), Utils.getPairOfVideos(videos, it))

                        val header = HeaderItem(rowsAdapter.size().toLong(), null)
                        rowsAdapter.add(ListRow(header, cardsAdapter))

                    }
                }
            }

        }
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (!TextUtils.isEmpty(query)) {
            if (query != null) {
                ChikiFetcher().searchForVideos(query).observe(this) { videos ->
                    ChikiChikiDatabaseRepository.get().getAllWatchedVideos().observe(this) {
                        rowsAdapter.clear()
                        cardsAdapter.clear()
                        cardsAdapter.addAll(cardsAdapter.size(), Utils.getPairOfVideos(videos, it))

                        val header = HeaderItem(rowsAdapter.size().toLong(), null)
                        rowsAdapter.add(ListRow(header, cardsAdapter))

                    }
                }
            }

        }
        return true
    }

    private fun setupBackgroundAnimation() {
        val container = this.view?.findViewById<FrameLayout>(androidx.leanback.R.id.lb_search_frame)
        container.apply {

            //set animation to background
            container?.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.grain_animation)
            grainAnimation = this?.background as AnimationDrawable
        }
        grainAnimation.start()
    }


}