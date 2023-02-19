package tube.chikichiki.sako.fragment

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import tube.chikichiki.sako.R
import tube.chikichiki.sako.Utils
import tube.chikichiki.sako.adapter.VideoAdapter
import tube.chikichiki.sako.api.ChikiFetcher
import tube.chikichiki.sako.database.ChikiChikiDatabaseRepository
import tube.chikichiki.sako.model.Banner
import tube.chikichiki.sako.model.Language
import tube.chikichiki.sako.model.Video
import tube.chikichiki.sako.model.VideoChannel
import java.util.*

private const val ARG_CHANNEL_HANDLE = "CHANNEL_HANDLE"

class ChannelVideosFragment : Fragment(R.layout.fragment_channel_videos),
    VideoAdapter.VideoViewClick {
    private lateinit var grainAnimation: AnimationDrawable
    private lateinit var channelVideosRecyclerView: RecyclerView
    private val videoAdapter: VideoAdapter by lazy { VideoAdapter() }
    private lateinit var currentListOfVideos: MutableList<Video>
    private var loadStartNumber: Int = 100
    private var channelHandle: String? = null
    private var isLoading = false
    private val sortArray: Array<String> = arrayOf("Recent", "Most popular", "Duration", "Alphabet")
    private val sortToArray =
        arrayOf("-createdAt", "-views", "-duration", "name") // must match sortArray
    private var showRaws = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val motionLayout: MotionLayout = view.findViewById(R.id.channel_videos_constraint_layout)
        channelVideosRecyclerView = view.findViewById(R.id.channel_videos_recycler_view)
        val sortSpinner: Spinner = view.findViewById(R.id.sort_spinner)
        val searchImageView: ImageButton = view.findViewById(R.id.search_image)
        val searchEditText: EditText = view.findViewById(R.id.searchView)
        val searchBackImageView: ImageButton = view.findViewById(R.id.search_back)
        val showRawCheckBox: CheckBox = view.findViewById(R.id.raws_checkBox)


        //get channel handle from fragment arguments
        channelHandle = arguments?.getString(ARG_CHANNEL_HANDLE)

        //set up recycler view layout manager and adapter
        channelVideosRecyclerView.layoutManager = LinearLayoutManager(context)

        channelVideosRecyclerView.adapter = videoAdapter

        //set up sort by spinner

        sortSpinner.adapter = context?.let { context ->
            ArrayAdapter(context, R.layout.item_custom_spinner, sortArray)
        }

        //get videos from api and set them up to recycler view based on selected sort by method in spinner (default is recent as page loads)
        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //clear list before loading
                videoAdapter.submitList(listOf())
                loadVideos(p2, view)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }


        //set fragment background animation and start it
        motionLayout.apply {
            setBackgroundResource(R.drawable.grain_animation)
            grainAnimation = background as AnimationDrawable
        }
        grainAnimation.start()


        //retrieve more videos from api if scrolled down far enough in recycler view
        channelVideosRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                if (currentListOfVideos.size < 10) {
                    if (linearLayoutManager.findLastVisibleItemPosition() == currentListOfVideos.size - 1) {
                        if (!isLoading) {

                            loadMore(sortSpinner.selectedItemPosition)
                            isLoading = true
                        }
                    }
                } else {
                    if (linearLayoutManager.findLastVisibleItemPosition() == currentListOfVideos.size - 10) {
                        if (!isLoading) {

                            loadMore(sortSpinner.selectedItemPosition)
                            isLoading = true
                        }

                    }
                }


            }
        })

        //toggle search view
        searchImageView.setOnClickListener {

            //show edit text and back button
            searchEditText.visibility = View.VISIBLE
            searchBackImageView.visibility = View.VISIBLE

            //hide spinner , search button and checkbox
            sortSpinner.visibility = View.INVISIBLE
            it.visibility = View.INVISIBLE
            showRawCheckBox.visibility = View.INVISIBLE

            //focus on search edit text and show keyboard
            searchEditText.requestFocus()
            val input: InputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            input.showSoftInput(searchEditText, 0)


        }

        searchBackImageView.setOnClickListener {

            //hide search edit text and back button
            searchEditText.visibility = View.GONE
            it.visibility = View.GONE

            //show spinner , search button and check box
            sortSpinner.visibility = View.VISIBLE
            searchImageView.visibility = View.VISIBLE
            showRawCheckBox.visibility = View.VISIBLE

            //clear search edit text
            searchEditText.setText("")

            //reset search results
            loadVideos(sortSpinner.selectedItemPosition, view)


            //hide keyboard
            val input: InputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            input.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        }

        //search edit text listener
        searchEditText.setOnEditorActionListener { _, i, _ ->
            if (searchEditText.text.isNotEmpty()) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    searchChannel(channelHandle, searchEditText.text.toString())
                }
            }

            //hide keyboard
            val input: InputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            input.hideSoftInputFromWindow(searchEditText.windowToken, 0)

        }


        //show raws check box listener
        showRawCheckBox.setOnCheckedChangeListener { _, p1 ->
            showRaws = p1
            loadVideos(sortSpinner.selectedItemPosition, view)
        }
        tabLayoutOnReselectGoToPositionZero()
    }

    // retrieve videos from api based on current list size as a page start point
    private fun loadMore(sortPos: Int) {
        Log.d("TESTLOG", "Start Number : $loadStartNumber")
        //get sorted videos based on spinner position
        val sort = sortToArray[sortPos]


        channelHandle?.let { channel ->
            ChikiFetcher().fetchVideosOfaChannel(channel, loadStartNumber, sortBy = sort).observe(
                viewLifecycleOwner
            ) { list ->


                ChikiChikiDatabaseRepository.get().getAllWatchedVideos()
                    .observe(viewLifecycleOwner) {

                        var videos = list.toMutableList()
                        Log.d("TESTLOG", "VIDEO SIZE BEFORE ${videos.size}")

                        //if there are no videos (english and japanese) stop recursion
                        if (videos.size == 0) {
                            isLoading = false

                            //remove loading progress bar from currentlistofvideos position at last index since there are no more videos left
                            addEndOfVideosLine()


                            videoAdapter.submitList(
                                Utils.getPairOfVideos(
                                    currentListOfVideos,
                                    it
                                )
                            )


                            return@observe
                        }


                        //if user choses to hide raws
                        if (!showRaws) {
                            videos = videos.filter { it.language.id == "en" }.toMutableList()
                        }
                        loadStartNumber += 100
                        Log.d("TESTLOG", "VIDEO SIZE after ${videos.size}")
                        //if there are no english videos from request , request next batch
                        if (videos.size <= 5) {
                            loadMore(sortPos)
                        }

                        //remove loading progress bar from last index before appending more video into it
                        removeLoadMoreProgressBar()

                        currentListOfVideos =
                            (currentListOfVideos + videos).toMutableList() //add lists to get all available videos size

                        //add loading progress bar after the last item
                        addLoadMoreProgressBar()
                        videoAdapter.submitList(
                            Utils.getPairOfVideos(
                                currentListOfVideos,
                                it
                            )
                        ) //load new videos in recyclerview

                        isLoading = false

                    }
            }
        }
    }

    //get videos from api and set them up to recycler view
    private fun loadVideos(spinnerPosition: Int, view: View) {

        val sort = sortToArray[spinnerPosition]
        val noVideosTextview = view.findViewById(R.id.no_channel_videos_found_text_view) as TextView
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
        loadStartNumber = 100

        //show progress bar if sort by was changed
        if (progressBar.visibility == View.GONE) {
            progressBar.visibility = View.VISIBLE
            noVideosTextview.visibility = View.GONE

        }

        channelHandle?.let {
            ChikiFetcher().fetchVideosOfaChannel(it, sortBy = sort).observe(
                viewLifecycleOwner
            ) { list ->
                ChikiChikiDatabaseRepository.get().getAllWatchedVideos()
                    .observe(viewLifecycleOwner) {


                        var videos = list.toMutableList()
                        //if user choses to hide raws
                        if (!showRaws) {
                            videos = videos.filter { it.language.id == "en" }.toMutableList()
                        }
                        //add loading progress bar after the last item
                        if (videos.isNotEmpty()) {
                            videos.add(
                                Video(
                                    UUID.randomUUID(), "", "", "", VideoChannel(
                                        -1, "",
                                        Banner(), "", ""
                                    ), 0, language = Language("", ""), 1
                                )
                            )
                        }


                        //apply recycler view adapter with retrieved list
                        channelVideosRecyclerView.apply {
                            videoAdapter.submitList(Utils.getPairOfVideos(videos, it))
                            videoAdapter.setVideoViewClickListener(this@ChannelVideosFragment)

                        }

                        currentListOfVideos = videos.toMutableList()

                        //if there are no videos for the channel show text view
                        if (list.isEmpty()) {
                            noVideosTextview.text = getString(R.string.no_channel_videos_found)
                            noVideosTextview.visibility = View.VISIBLE
                        } else if (videos.isEmpty()) {

                            noVideosTextview.text =
                                getString(R.string.no_english_channel_videos_found)
                            noVideosTextview.visibility = View.VISIBLE
                        }
                        //hide loading bar after loading list
                        progressBar.visibility = View.GONE

                    }
            }
        }
    }

    private fun tabLayoutOnReselectGoToPositionZero() {

        val tabLayout = activity?.findViewById<TabLayout>(R.id.channel_tab_layout)
        tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                if (tab?.text == "Videos") {
                    channelVideosRecyclerView.smoothScrollToPosition(0)
                }
            }

        })
    }

    private fun searchChannel(channelHandle: String?, searchText: String) {
        val progressBar = view?.findViewById<ProgressBar>(R.id.progressBar)
        val tempList: MutableList<Video> = mutableListOf()

        //show loading progress bar while getting the search results
        if (progressBar?.visibility == View.GONE) {
            videoAdapter.submitList(emptyList()) // empty recycler view as the video list will change
            progressBar.visibility = View.VISIBLE
        }


        ChikiFetcher().searchForVideos(searchText).observe(viewLifecycleOwner) { list ->

            ChikiChikiDatabaseRepository.get().getAllWatchedVideos().observe(viewLifecycleOwner) {
                //get all videos from the current channel
                list.forEach {
                    if (it.videoChannel.channelHandle == channelHandle) {
                        tempList.add(it)
                    }
                }

                //update recycler view adapter with the new list
                videoAdapter.submitList(Utils.getPairOfVideos(tempList, it))
                //remove progress bar after loading
                progressBar?.visibility = View.GONE
            }
        }


    }

    private fun removeLoadMoreProgressBar() {
        if (currentListOfVideos.isNotEmpty()) {
            if (currentListOfVideos[currentListOfVideos.size - 1].getUsedLayout() == R.layout.list_item_loader) {
                currentListOfVideos.removeLast()
                videoAdapter.notifyItemRemoved(currentListOfVideos.size)
            }
        }
    }

    private fun addLoadMoreProgressBar() {
        currentListOfVideos.add(
            Video(
                UUID.randomUUID(), "", "", "", VideoChannel(
                    -1, "",
                    Banner(), "", ""
                ), 0, language = Language("", ""), 1
            )
        )
        videoAdapter.notifyItemInserted(currentListOfVideos.size - 1)
    }

    private fun addEndOfVideosLine() {
        if (currentListOfVideos.isNotEmpty()) {
            if (currentListOfVideos[currentListOfVideos.size - 1].getUsedLayout() == R.layout.list_item_loader) {
                currentListOfVideos.removeLast()
                videoAdapter.notifyItemRemoved(currentListOfVideos.size)
                currentListOfVideos.add(
                    Video(
                        UUID.randomUUID(), "", "", "", VideoChannel(
                            -1, "",
                            Banner(), "", ""
                        ), 0, language = Language("", ""), 2
                    )
                )
                videoAdapter.notifyItemInserted(currentListOfVideos.size - 1)
            }
        }
    }


    companion object {
        //returns a bundle based on arg_channel_id string key
        fun getVideoArgsBundle(channelHandle: String): Bundle {
            return bundleOf(ARG_CHANNEL_HANDLE to channelHandle)
        }


    }

    override fun onVideoClick(
        videoId: UUID,
        videoName: String,
        videoDescription: String,
        previewPath: String,
        duration: Int
    ) {

        activity?.supportFragmentManager?.beginTransaction()?.apply {
            setCustomAnimations(R.anim.slide_up, 0)
            replace(
                R.id.video_container,
                VideoPlayerFragment.newInstance(
                    videoId,
                    videoName,
                    videoDescription,
                    previewPath,
                    duration
                )
            )
            commit()
        }
    }


}