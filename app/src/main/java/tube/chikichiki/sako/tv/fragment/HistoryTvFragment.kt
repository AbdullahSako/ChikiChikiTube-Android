package tube.chikichiki.sako.tv.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.ViewModelProvider
import tube.chikichiki.sako.R
import tube.chikichiki.sako.database.ChikiChikiDatabaseRepository
import tube.chikichiki.sako.model.HistoryVideoInfo
import tube.chikichiki.sako.tv.activity.TVVideoPlayerActivity
import tube.chikichiki.sako.tv.presenter.HistoryVideoTvPresenter
import tube.chikichiki.sako.viewModel.MostViewedVideosViewModel

class HistoryTvFragment : VerticalGridSupportFragment(),
    BrowseSupportFragment.MainFragmentAdapterProvider, OnItemViewClickedListener,
    OnItemViewSelectedListener {
    private lateinit var mGridAdapter: ArrayObjectAdapter
    private val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_MEDIUM
    private var mostViewedVideosViewModel: MostViewedVideosViewModel? = null
    private var historyItemList: List<HistoryVideoInfo> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mostViewedVideosViewModel =
            activity?.let { ViewModelProvider(it).get(MostViewedVideosViewModel::class.java) }

        setupUi()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitleViewListeners()
        setTitleFontAndColor()


    }

    private fun setupUi() {

        val gridPresenter = VerticalGridPresenter(ZOOM_FACTOR)
        gridPresenter.numberOfColumns = 3
        this.onItemViewClickedListener = this
        this.setOnItemViewSelectedListener(this)
        setGridPresenter(gridPresenter)

        mGridAdapter = ArrayObjectAdapter(HistoryVideoTvPresenter())
        adapter = mGridAdapter

        prepareEntranceTransition()
        title = getString(R.string.history)


        loadAndShowMostViewedVideos()


    }

    private fun loadAndShowMostViewedVideos() {

        ChikiChikiDatabaseRepository.get().getHistoryBig().observe(this) {
            if (it.isEmpty()) {
                showNoHistoryVideosTextView()
            }

            if (mGridAdapter.size() != 0) { //a change in history list (after opening a video in this fragment) causes videos to duplicate
                mGridAdapter.clear()
            }
            historyItemList = it
            mGridAdapter.addAll(mGridAdapter.size(), it)
            startEntranceTransition()

        }


    }

    private fun showNoHistoryVideosTextView() {
        val textView = TextView(activity)
        textView.text = getString(R.string.history_no_videos)
        textView.setTextColor(ContextCompat.getColor(requireActivity(), R.color.orange))
        textView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply { gravity = Gravity.CENTER }
        textView.textSize = 24f
        textView.typeface = ResourcesCompat.getFont(requireActivity(), R.font.mochiypoppone)

        view?.findViewById<FrameLayout>(androidx.leanback.R.id.browse_grid_dock)?.addView(textView)
    }

    private fun setTitleViewListeners() {

        //clear all button
        titleView?.findViewById<Button>(R.id.tv_history_title_view_button)?.setOnClickListener {
            if (historyItemList.isNotEmpty()) {
                historyItemList.forEach {
                    ChikiChikiDatabaseRepository.get().removeFromHistory(it)

                    ChikiChikiDatabaseRepository.get().getWatchedVideo(it.uuid)
                        .observe(viewLifecycleOwner) { watched ->
                            if (watched != null) {
                                ChikiChikiDatabaseRepository.get().removeWatchedVideo(watched)
                            }
                        }

                }
            } else {
                Toast.makeText(
                    requireActivity(), "There are no videos to clear", Toast.LENGTH_SHORT
                ).show()
            }
        }


    }

    private fun setTitleFontAndColor() {
        val textView = view?.findViewById<TextView>(androidx.leanback.R.id.title_text)
        textView?.setTextColor(ContextCompat.getColor(requireActivity(), R.color.font_pink))
        textView?.typeface = ResourcesCompat.getFont(requireActivity(), R.font.mochiypoppone)

    }

    override fun onInflateTitleView(
        inflater: LayoutInflater?, parent: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        //use custom title view
        if (inflater != null) {
            return inflater.inflate(R.layout.layout_tv_custom_history_title_view, parent, false)
                .also {

                    val textView = it.findViewById<TextView>(androidx.leanback.R.id.title_text)
                    val newParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.START
                    }
                    textView.apply {
                        layoutParams = newParams
                        background = null
                    }
                }
        }

        return super.onInflateTitleView(inflater, parent, savedInstanceState)
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
        val video = item as HistoryVideoInfo
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
