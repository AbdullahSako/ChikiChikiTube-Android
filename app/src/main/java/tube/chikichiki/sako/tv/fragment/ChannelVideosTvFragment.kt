package tube.chikichiki.sako.tv.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import tube.chikichiki.sako.model.Video
import tube.chikichiki.sako.model.VideoAndWatchedTimeModel
import tube.chikichiki.sako.tv.activity.TVVideoPlayerActivity
import tube.chikichiki.sako.tv.presenter.VideoTvPresenter

private const val ARG_CHANNEL_HANDLE = "CHANNELHANDLE"
private const val ARG_CHANNEL_DISPLAY_NAME="CHANNELDISPLAYNAME"
class ChannelVideosTvFragment: VerticalGridSupportFragment(),
    BrowseSupportFragment.MainFragmentAdapterProvider, OnItemViewClickedListener,
    OnItemViewSelectedListener {

    private var loadStartNumber:Int = 100
    private var channelHandle:String? = null
    private var isLoading=false
    private lateinit var gridLayoutManager:GridLayoutManager
    private lateinit var mGridAdapter: ArrayObjectAdapter
    private val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_MEDIUM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUi()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setTitleFontAndColor()
        setTitleViewListeners()
        setupRecyclerViewOnScrollListener()

    }


    private fun setupUi(){

        val gridPresenter = VerticalGridPresenter(ZOOM_FACTOR)
        gridPresenter.numberOfColumns = 3
        this.onItemViewClickedListener = this
        this.setOnItemViewSelectedListener(this)
        setGridPresenter(gridPresenter)


        //set arguments
        title = arguments?.getString(ARG_CHANNEL_DISPLAY_NAME)
        try{
            if(arguments?.getString(ARG_CHANNEL_HANDLE) != null){
                channelHandle = arguments?.getString(ARG_CHANNEL_HANDLE)!!
            }
        }
        catch (e:Exception){
            e.printStackTrace()
        }


        mGridAdapter = ArrayObjectAdapter(VideoTvPresenter())
        adapter =mGridAdapter
        prepareEntranceTransition()



        loadVideos(0)



    }

    private fun loadAndShowChannelVideos(){

        if (channelHandle != null) {
            ChikiFetcher().fetchVideosOfaChannel(channelHandle!!).observe(this){
                mGridAdapter.addAll(mGridAdapter.size(),it)
                startEntranceTransition()
            }
        }

    }

    private fun setTitleFontAndColor(){
        val textView=view?.findViewById<TextView>(androidx.leanback.R.id.title_text)
        textView?.setTextColor(ContextCompat.getColor(requireActivity(), R.color.font_pink))
        textView?.typeface = ResourcesCompat.getFont(requireActivity(), R.font.mochiypoppone)

    }

    private fun setTitleViewListeners(){

    }

    private fun setupRecyclerViewOnScrollListener(){

        view?.findViewById<VerticalGridView>(androidx.leanback.R.id.browse_grid)?.addOnScrollListener(object :OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                gridLayoutManager=recyclerView.layoutManager as GridLayoutManager


                //get current 10 last child Views

                val last10ChildView = arrayListOf<View?>()
                    for(i in 1..10){
                         last10ChildView.add(gridLayoutManager.getChildAt(
                             gridLayoutManager.childCount - i
                         ))
                    }

                //get the bottom
                val last10ChildBottom = arrayListOf<Int?>()
                last10ChildView.forEach { last10ChildBottom.add(it?.bottom) }

                //get last childview's position
                val last10Positions = arrayListOf<Int?>()

                last10ChildView.forEach { last10Positions.add(it?.let { it1 ->
                    gridLayoutManager.getPosition(
                        it1
                    )
                }) }


                if (last10Positions.contains(gridLayoutManager.itemCount.minus(5))) {

                    if(!isLoading){
                        loadMore(0)
                        isLoading = true
                    }

                }
            }
        })

    }

    //get videos from api and set them up to adapter
    private fun loadVideos(spinnerPosition: Int){

//        val sort=getSort(spinnerPosition)
//        val noVideosTextview=view.findViewById(R.id.no_channel_videos_found_text_view) as TextView
//        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
        loadStartNumber=100

//        //show progress bar if sort by was changed
//        if(progressBar.visibility==View.GONE){
//            progressBar.visibility=View.VISIBLE
//            noVideosTextview.visibility=View.GONE
//
//        }

        channelHandle?.let { ChikiFetcher().fetchVideosOfaChannel(it).observe(this
        ) { list ->
            ChikiChikiDatabaseRepository.get().getAllWatchedVideos().observe(this){


                var videos = list.toMutableList()
//                //if user choses to hide raws
//                if(!showRaws){
//                    videos=videos.filter { it.description.contains("en") }.toMutableList()
//                }
//                //add loading progress bar after the last item
//                if(videos.isNotEmpty()) {
//                    videos.add(
//                        Video(
//                            UUID.randomUUID(), "", "", "", VideoChannel(
//                                -1, "",
//                                Banner(), "", ""
//                            ), 0, 1
//                        )
//                    )
//                }

                //apply recycler view adapter with retrieved list
                mGridAdapter.addAll(mGridAdapter.size(),Utils.getPairOfVideos(videos,it))

                startEntranceTransition()
                //if there are no videos for the channel show text view
//                if (list.isEmpty()) {
//                    noVideosTextview.text=getString(R.string.no_channel_videos_found)
//                    noVideosTextview.visibility = View.VISIBLE
//                }
//                else if(videos.isEmpty()){
//
//                    noVideosTextview.text=getString(R.string.no_english_channel_videos_found)
//                    noVideosTextview.visibility=View.VISIBLE
//                }
//                //hide loading bar after loading list
//                progressBar.visibility = View.GONE

            }
        }
        }
    }

    private fun loadMore(sortPos:Int){
        Log.d("TESTLOG","Start Number : $loadStartNumber")
        //get sorted videos based on spinner position
//        val sort=getSort(sortPos)


        channelHandle?.let { channel ->
            ChikiFetcher().fetchVideosOfaChannel(channel, loadStartNumber).observe(this
            ) { videos ->



                ChikiChikiDatabaseRepository.get().getAllWatchedVideos().observe(this){

                    Log.d("TESTLOG","VIDEO SIZE BEFORE ${videos.size}")

                    //if there are no videos (english and japanese) stop recursion
                    if(videos.size==0){
                        isLoading = false

                        //remove loading progress bar from currentlistofvideos position at last index since there are no more videos left
                        //addEndOfVideosLine()

                        //mGridAdapter.clear()
                        mGridAdapter.addAll(mGridAdapter.size(),
                            Utils.getPairOfVideos(videos,
                            it
                        ))



                        return@observe
                    }


                    //if user choses to hide raws
//                    if(!showRaws){
//                        videos=videos.filter { it.description.contains("en") }.toMutableList()
//                    }
                    loadStartNumber += 100
                    Log.d("TESTLOG","VIDEO SIZE after ${videos.size}")
                    //if there are no english videos from request , request next batch
                    if(videos.size <= 5){
                        loadMore(sortPos)
                    }

                    //remove loading progress bar from last index before appending more video into it
                    //removeLoadMoreProgressBar()


                    //add loading progress bar after the last item
                    //addLoadMoreProgressBar()
                    //mGridAdapter.clear()

                    mGridAdapter.addAll(mGridAdapter.size(),Utils.getPairOfVideos(videos,it)) //load new videos in recyclerview


                    isLoading = false

                }
            }
        }
    }



    override fun onInflateTitleView(
        inflater: LayoutInflater?,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //use custom title view
        if (inflater != null) {
            //return CustomTitleView(requireActivity())
//            return inflater.inflate(R.layout.layout_tv_custom_channel_videos_title_view, parent, false).also {
//
//                val textView = it.findViewById<TextView>(androidx.leanback.R.id.title_text)
//                val newParams = FrameLayout.LayoutParams(
//                    FrameLayout  .LayoutParams.WRAP_CONTENT,
//                    FrameLayout .LayoutParams.WRAP_CONTENT
//                ).apply {
//                    gravity = Gravity.START
//                }
//                textView.apply {
//                    layoutParams = newParams
//                    background = null
//                }
//            }
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
        ChikiFetcher().fetchStreamingPlaylist(videoItem.video.uuid).observe(this){
            progressBarManager.hide()
            val intent = TVVideoPlayerActivity.newInstance(activity,videoItem.video.uuid.toString(),videoItem.video.name,videoItem.video.description,videoItem.video.previewPath,videoItem.video.duration)
            startActivity(intent)

        }
    }

    override fun onItemSelected(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {

    }



    companion object{
        fun newInstance(channelHandle: String?,displayName:String): ChannelVideosTvFragment {
            val args = Bundle()
            args.putString(ARG_CHANNEL_HANDLE, channelHandle)
            args.putString(ARG_CHANNEL_DISPLAY_NAME,displayName)
            val fragment = ChannelVideosTvFragment()
            fragment.arguments = args
            return fragment
        }
    }
}