package tube.chikichiki.sako.tv.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.BrowseSupportFragment.MainFragmentAdapter
import androidx.leanback.app.BrowseSupportFragment.MainFragmentAdapterProvider
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.ViewModelProvider
import tube.chikichiki.sako.R
import tube.chikichiki.sako.Utils
import tube.chikichiki.sako.database.ChikiChikiDatabaseRepository
import tube.chikichiki.sako.model.VideoAndWatchedTimeModel
import tube.chikichiki.sako.tv.activity.TVVideoPlayerActivity
import tube.chikichiki.sako.tv.presenter.VideoTvPresenter
import tube.chikichiki.sako.viewModel.MostViewedVideosViewModel

class MostViewedVideosTvFragment : VerticalGridSupportFragment(), MainFragmentAdapterProvider,
    OnItemViewClickedListener, OnItemViewSelectedListener {
    private lateinit var mGridAdapter: ArrayObjectAdapter
    private val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_MEDIUM
    private var mostViewedVideosViewModel: MostViewedVideosViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mostViewedVideosViewModel =
            activity?.let { ViewModelProvider(it).get(MostViewedVideosViewModel::class.java) }

        setupUi()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitleFontAndColor()
    }


    private fun setupUi() {

        val gridPresenter = VerticalGridPresenter(ZOOM_FACTOR)
        gridPresenter.numberOfColumns = 3
        this.onItemViewClickedListener = this
        this.setOnItemViewSelectedListener(this)
        setGridPresenter(gridPresenter)

        mGridAdapter = ArrayObjectAdapter(VideoTvPresenter())
        adapter = mGridAdapter

        title = getString(R.string.most_viewed)


        prepareEntranceTransition()



        loadAndShowMostViewedVideos()


    }

    private fun loadAndShowMostViewedVideos() {

        mostViewedVideosViewModel?.mostViewedVideosLiveData?.observe(this) { videos ->

            ChikiChikiDatabaseRepository.get().getAllWatchedVideos().observe(this) {
                mGridAdapter.addAll(mGridAdapter.size(), Utils.getPairOfVideos(videos, it))
                startEntranceTransition()


            }


        }

    }

    private fun setTitleFontAndColor() {
        val textView = view?.findViewById<TextView>(androidx.leanback.R.id.title_text)
        textView?.setTextColor(ContextCompat.getColor(requireActivity(), R.color.font_pink))
        textView?.typeface = ResourcesCompat.getFont(requireActivity(), R.font.mochiypoppone)

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
        val videoItem = item as VideoAndWatchedTimeModel
        progressBarManager.hide()
        val intent = TVVideoPlayerActivity.newInstance(
            activity,
            videoItem.video.uuid.toString(),
            videoItem.video.name,
            videoItem.video.description,
            videoItem.video.previewPath,
            videoItem.video.duration
        )
        startActivity(intent)


    }

    override fun onItemSelected(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {

    }


}

