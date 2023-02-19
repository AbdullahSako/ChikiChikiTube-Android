package tube.chikichiki.sako.tv.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import tube.chikichiki.sako.R
import tube.chikichiki.sako.database.ChikiChikiDatabaseRepository
import tube.chikichiki.sako.model.WatchLater
import tube.chikichiki.sako.tv.activity.TVVideoPlayerActivity
import tube.chikichiki.sako.tv.presenter.WatchLaterTvPresenter

class WatchLaterTvFragment : VerticalGridSupportFragment(),
    BrowseSupportFragment.MainFragmentAdapterProvider, OnItemViewClickedListener,
    OnItemViewSelectedListener {
    private lateinit var mGridAdapter: ArrayObjectAdapter
    private val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_MEDIUM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        mGridAdapter = ArrayObjectAdapter(WatchLaterTvPresenter())
        adapter = mGridAdapter

        title = getString(R.string.watch_later)

        prepareEntranceTransition()


        loadAndShowMostViewedVideos()


    }

    private fun loadAndShowMostViewedVideos() {

        ChikiChikiDatabaseRepository.get().getAllWatchLater().observe(this) { watchLaterList ->
            if (watchLaterList.isEmpty()) {
                showNoWatchLaterVideosTextView()
            }

            if (mGridAdapter.size() != 0) { //a change in watch later list (after opening a video in this fragment and removing watch later) causes videos to duplicate
                mGridAdapter.clear()
            }

            mGridAdapter.addAll(mGridAdapter.size(), watchLaterList)
            startEntranceTransition()
        }


    }


    private fun showNoWatchLaterVideosTextView() {
        val textView = TextView(activity)
        textView.text = getString(R.string.watch_later_no_videos)
        textView.setTextColor(ContextCompat.getColor(requireActivity(), R.color.orange))
        textView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply { gravity = Gravity.CENTER }
        textView.textSize = 24f
        textView.typeface = ResourcesCompat.getFont(requireActivity(), R.font.mochiypoppone)

        view?.findViewById<FrameLayout>(androidx.leanback.R.id.browse_grid_dock)?.addView(textView)
    }

    private fun setTitleFontAndColor() {
        val textView = view?.findViewById<TextView>(androidx.leanback.R.id.title_text)
        textView?.setTextColor(ContextCompat.getColor(requireActivity(), R.color.font_pink))
        textView?.typeface = ResourcesCompat.getFont(requireActivity(), R.font.mochiypoppone)

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
        val video = item as WatchLater
        progressBarManager.hide()
        val intent = TVVideoPlayerActivity.newInstance(
            activity,
            video.uuid.toString(),
            video.name,
            video.description,
            video.previewPath,
            video.duration
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