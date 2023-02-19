package tube.chikichiki.sako.tv.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import tube.chikichiki.sako.R
import tube.chikichiki.sako.Utils
import tube.chikichiki.sako.api.ChikiFetcher
import tube.chikichiki.sako.database.ChikiChikiDatabaseRepository
import tube.chikichiki.sako.model.VideoAndWatchedTimeModel
import tube.chikichiki.sako.tv.activity.TVVideoPlayerActivity
import tube.chikichiki.sako.tv.presenter.VideoTvPresenter

private const val ARG_CHANNEL_HANDLE = "CHANNELHANDLE"
private const val ARG_CHANNEL_DISPLAY_NAME = "CHANNELDISPLAYNAME"

class ChannelVideosTvFragment : VerticalGridSupportFragment(),
    BrowseSupportFragment.MainFragmentAdapterProvider, OnItemViewClickedListener,
    OnItemViewSelectedListener {

    private var loadStartNumber: Int = 100
    private var channelHandle: String? = null
    private var isLoading = false
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var mGridAdapter: ArrayObjectAdapter
    private val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_MEDIUM

    private val sortArray: Array<String> = arrayOf("Recent", "Most popular", "Duration", "Alphabet")
    private val sortToArray =
        arrayOf("-createdAt", "-views", "-duration", "name") // must match sortArray
    private var showRaws = false
    private var selectedSortPosition: Int = 0

    private lateinit var noEnglishVideosTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUi()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadVideos()
        setTitleFontAndColor()
        setTitleViewListeners()
        setupRecyclerViewOnScrollListener()
        setupFilter()


    }


    private fun setupUi() {

        val gridPresenter = VerticalGridPresenter(ZOOM_FACTOR)
        gridPresenter.numberOfColumns = 3
        this.onItemViewClickedListener = this
        this.setOnItemViewSelectedListener(this)
        setGridPresenter(gridPresenter)


        //set arguments
        title = arguments?.getString(ARG_CHANNEL_DISPLAY_NAME)
        try {
            if (arguments?.getString(ARG_CHANNEL_HANDLE) != null) {
                channelHandle = arguments?.getString(ARG_CHANNEL_HANDLE)!!
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        mGridAdapter = ArrayObjectAdapter(VideoTvPresenter())
        adapter = mGridAdapter
        prepareEntranceTransition()

        //default initialization
        noEnglishVideosTextView = TextView(requireActivity())


    }


    private fun setTitleFontAndColor() {
        val textView = view?.findViewById<TextView>(androidx.leanback.R.id.title_text)
        textView?.setTextColor(ContextCompat.getColor(requireActivity(), R.color.font_pink))
        textView?.typeface = ResourcesCompat.getFont(requireActivity(), R.font.mochiypoppone)

    }

    private fun setTitleViewListeners() {

    }

    private fun setupRecyclerViewOnScrollListener() {

        view?.findViewById<VerticalGridView>(androidx.leanback.R.id.browse_grid)
            ?.addOnScrollListener(object : OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    gridLayoutManager = recyclerView.layoutManager as GridLayoutManager


                    //get current 10 last child Views
                    val last10ChildView = arrayListOf<View?>()
                    for (i in 1..10) {
                        last10ChildView.add(
                            gridLayoutManager.getChildAt(
                                gridLayoutManager.childCount - i
                            )
                        )
                    }

                    //get the bottom
                    val last10ChildBottom = arrayListOf<Int?>()
                    last10ChildView.forEach { last10ChildBottom.add(it?.bottom) }

                    //get last childview's position
                    val tenVisibleItemPositions = arrayListOf<Int?>()

                    last10ChildView.forEach {
                        tenVisibleItemPositions.add(it?.let { it1 ->
                            gridLayoutManager.getPosition(
                                it1
                            )
                        })
                    }

                    //two conditions because in some cases the last item is counted as not shown
                    //check if last 5th item is visible
                    if (tenVisibleItemPositions.contains(gridLayoutManager.itemCount.minus(5))) {
                        if (!isLoading) {
                            loadMore()
                            isLoading = true
                        }

                    }
                    //check if last item item is visible (sometimes sorting returns less than 5 items)
                    else if (tenVisibleItemPositions.contains(gridLayoutManager.itemCount - 1)) {
                        if (!isLoading) {
                            loadMore()
                            isLoading = true
                        }
                    }
                }
            })

    }

    //get videos from api and set them up to adapter
    private fun loadVideos() {

        val sort = sortToArray[selectedSortPosition]

        loadStartNumber = 100


        //hide no english videos text view incase shown
        hideNoEnglishVideosTextView()


        channelHandle?.let {
            ChikiFetcher().fetchVideosOfaChannel(it, sortBy = sort).observe(
                viewLifecycleOwner
            ) { list ->
                ChikiChikiDatabaseRepository.get().getAllWatchedVideos()
                    .observe(viewLifecycleOwner) {


                        var videos = list.toMutableList()

                        //if user choses to hide raws
                        if (!showRaws) {
                            videos = videos.filter { it.description.contains("en") }.toMutableList()
                        }


                        //apply recycler view adapter with retrieved list
                        mGridAdapter.clear()
                        mGridAdapter.addAll(mGridAdapter.size(), Utils.getPairOfVideos(videos, it))


                        //if there are no videos for the channel show text view
                        if (list.isEmpty()) {
                            showNoVideosTextView()
                        } else if (videos.isEmpty()) {
                            showNoEnglishVideosTextView()
                        }

                        startEntranceTransition()

                    }
            }
        }
    }

    private fun loadMore() {
        Log.d("TESTLOG", "Start Number : $loadStartNumber")
        //get sorted videos based on spinner position
        val sort = sortToArray[selectedSortPosition]


        channelHandle?.let { channel ->
            ChikiFetcher().fetchVideosOfaChannel(channel, loadStartNumber, sortBy = sort).observe(
                this
            ) { vids ->
                var videos = vids

                ChikiChikiDatabaseRepository.get().getAllWatchedVideos().observe(this) {

                    Log.d("TESTLOG", "VIDEO SIZE BEFORE ${videos.size}")

                    //if there are no videos (english and japanese) stop recursion
                    if (videos.size == 0) {
                        isLoading = false

                        mGridAdapter.addAll(
                            mGridAdapter.size(),
                            Utils.getPairOfVideos(
                                videos,
                                it
                            )
                        )



                        return@observe
                    }


                    //if user chooses to hide raws
                    if (!showRaws) {
                        videos = videos.filter { it.description.contains("en") }.toMutableList()
                    }
                    loadStartNumber += 100
                    Log.d("TESTLOG", "VIDEO SIZE after ${videos.size}")
                    //if there are no english videos from request , request next batch
                    if (videos.size <= 5) {
                        loadMore()
                    }

                    mGridAdapter.addAll(
                        mGridAdapter.size(),
                        Utils.getPairOfVideos(videos, it)
                    ) //load new videos in recyclerview


                    isLoading = false

                }
            }
        }
    }


    private fun setupFilter() {


        val dialogView = layoutInflater.inflate(R.layout.layout_tv_filter_custom_alert_dialog, null)

        val spinner = dialogView?.findViewById<Spinner>(R.id.tv_filter_sort_spinner)

        val dialog = AlertDialog.Builder(requireActivity()).setPositiveButton(
            getString(R.string.apply),
            DialogInterface.OnClickListener { dialogInterface, i ->

                selectedSortPosition = spinner?.selectedItemPosition ?: 0
                showRaws =
                    dialogView.findViewById<CheckBox>(R.id.tv_filter_japanese_raw_check_box).isChecked
                loadVideos()

            }).setNegativeButton(
            R.string.cancel,
            DialogInterface.OnClickListener { dialogInterface, i ->

            }).setView(dialogView).create()


        spinner?.adapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_dropdown_item,
            sortArray
        )

        //more image button on click listener
        view?.findViewById<ImageButton>(R.id.tv_channel_more_img_btn)?.setOnClickListener {
            dialog.show()
        }

    }

    private fun showNoVideosTextView() {
        val textView = TextView(activity)
        textView.text = getString(R.string.no_channel_videos_found)
        textView.setTextColor(ContextCompat.getColor(requireActivity(), R.color.orange))
        textView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply { gravity = Gravity.CENTER }
        textView.textSize = 24f
        textView.typeface = ResourcesCompat.getFont(requireActivity(), R.font.mochiypoppone)

        view?.findViewById<FrameLayout>(androidx.leanback.R.id.browse_grid_dock)?.addView(textView)
    }


    private fun showNoEnglishVideosTextView() {
        noEnglishVideosTextView = TextView(activity)
        noEnglishVideosTextView.text = getString(R.string.no_english_channel_videos_found)
        noEnglishVideosTextView.setTextColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.orange
            )
        )
        noEnglishVideosTextView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply { gravity = Gravity.CENTER }
        noEnglishVideosTextView.textSize = 24f
        noEnglishVideosTextView.typeface =
            ResourcesCompat.getFont(requireActivity(), R.font.mochiypoppone)

        view?.findViewById<FrameLayout>(androidx.leanback.R.id.browse_grid_dock)
            ?.addView(noEnglishVideosTextView)
    }

    private fun hideNoEnglishVideosTextView() {
        view?.findViewById<FrameLayout>(androidx.leanback.R.id.browse_grid_dock)
            ?.removeView(noEnglishVideosTextView)
    }


    override fun onInflateTitleView(
        inflater: LayoutInflater?,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //use custom title view
        if (inflater != null) {
            return inflater.inflate(
                R.layout.layout_tv_custom_channel_videos_title_view,
                parent,
                false
            ).also {

                val textView = it.findViewById<TextView>(androidx.leanback.R.id.title_text)
                val newParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
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


    companion object {
        fun newInstance(channelHandle: String?, displayName: String): ChannelVideosTvFragment {
            val args = Bundle()
            args.putString(ARG_CHANNEL_HANDLE, channelHandle)
            args.putString(ARG_CHANNEL_DISPLAY_NAME, displayName)
            val fragment = ChannelVideosTvFragment()
            fragment.arguments = args
            return fragment
        }
    }
}