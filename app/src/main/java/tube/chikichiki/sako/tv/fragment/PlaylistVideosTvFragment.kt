package tube.chikichiki.sako.tv.fragment

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import androidx.recyclerview.widget.RecyclerView
import tube.chikichiki.sako.R
import tube.chikichiki.sako.Utils
import tube.chikichiki.sako.api.ChikiFetcher
import tube.chikichiki.sako.database.ChikiChikiDatabaseRepository
import tube.chikichiki.sako.model.VideoAndWatchedTimeModel
import tube.chikichiki.sako.tv.activity.TVVideoPlayerActivity
import tube.chikichiki.sako.tv.presenter.VideoTvPresenter

private const val ARG_PLAYLIST_ID = "PLAYLISTID"
private const val ARG_PLAYLIST_NAME = "PLAYLISTNAME"

class PlaylistVideosTvFragment : VerticalGridSupportFragment(), OnItemViewClickedListener,
    OnItemViewSelectedListener {

    private lateinit var grainAnimation: AnimationDrawable
    private lateinit var mGridAdapter: ArrayObjectAdapter
    private var isLoading = false
    private var playlistId: Int? = null
    private val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_MEDIUM


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupUi()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitleFontAndColor()
        startBackgroundAnimation()
        setupRecyclerViewOnScrollListener()

    }

    private fun setupUi() {
        val gridPresenter = VerticalGridPresenter(ZOOM_FACTOR)
        gridPresenter.numberOfColumns = 3
        this.onItemViewClickedListener = this
        this.setOnItemViewSelectedListener(this)
        setGridPresenter(gridPresenter)

        mGridAdapter = ArrayObjectAdapter(VideoTvPresenter())
        adapter = mGridAdapter
        prepareEntranceTransition()

        //get args
        try {
            if (arguments?.getInt(ARG_PLAYLIST_ID) != null) {
                playlistId = arguments?.getInt(ARG_PLAYLIST_ID)
            }

            title = arguments?.getString(ARG_PLAYLIST_NAME)

        } catch (e: Exception) {
            e.printStackTrace()
        }


        loadAndShowPlaylistVideos()


    }

    private fun loadAndShowPlaylistVideos() {

        if (playlistId != null) {
            ChikiFetcher().fetchVideosOfaPlaylist(playlistId!!).observe(this) {
                ChikiChikiDatabaseRepository.get().getAllWatchedVideos()
                    .observe(viewLifecycleOwner) { watchedTime ->

                        mGridAdapter.addAll(
                            mGridAdapter.size(),
                            Utils.getPairOfVideos(it, watchedTime)
                        )
                        startEntranceTransition()
                    }
            }
        }


    }

    private fun setupRecyclerViewOnScrollListener() {

        view?.findViewById<VerticalGridView>(androidx.leanback.R.id.browse_grid)
            ?.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val gridLayoutManager = recyclerView.layoutManager as GridLayoutManager


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
                    val last10Positions = arrayListOf<Int?>()

                    last10ChildView.forEach {
                        last10Positions.add(it?.let { it1 ->
                            gridLayoutManager.getPosition(
                                it1
                            )
                        })
                    }


                    if (last10Positions.contains(gridLayoutManager.itemCount.minus(5))) {

                        if (!isLoading) {
                            loadMore()
                            isLoading = true
                        }
                    }
                }
            })

    }

    // retrieve videos from api based on current list size as a page start point
    private fun loadMore() {
        playlistId?.let {
            ChikiFetcher().fetchVideosOfaPlaylist(it, mGridAdapter.size()).observe(
                this
            ) { list ->
                ChikiChikiDatabaseRepository.get().getAllWatchedVideos()
                    .observe(this) { watchedTime ->

                        mGridAdapter.addAll(
                            mGridAdapter.size(),
                            Utils.getPairOfVideos(list, watchedTime)
                        ) //load new videos in recyclerview

                        isLoading = false
                    }


            }
        }
    }

    private fun startBackgroundAnimation() {

        val container =
            this.view?.findViewById<FrameLayout>(androidx.leanback.R.id.browse_grid_dock)
        container.apply {

            //set animation to background
            container?.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.grain_animation)
            grainAnimation = this?.background as AnimationDrawable
        }
        grainAnimation.start()

    }

    private fun setTitleFontAndColor() {
        val textView = view?.findViewById<TextView>(androidx.leanback.R.id.title_text)
        textView?.setTextColor(ContextCompat.getColor(requireActivity(), R.color.font_pink))
        textView?.typeface = ResourcesCompat.getFont(requireActivity(), R.font.mochiypoppone)

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
        fun newInstance(playlistId: Int, playlistName: String): PlaylistVideosTvFragment {
            val args = Bundle()
            args.putInt(ARG_PLAYLIST_ID, playlistId)
            args.putString(ARG_PLAYLIST_NAME, playlistName)
            val fragment = PlaylistVideosTvFragment()
            fragment.arguments = args
            return fragment
        }
    }

}