package tube.chikichiki.fragment

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tube.chikichiki.R
import tube.chikichiki.adapter.VideoAdapter
import tube.chikichiki.api.ChikiFetcher
import tube.chikichiki.model.Video
import java.util.*

private const val ARG_CHANNEL_HANDLE="CHANNEL_HANDLE"

class ChannelVideosFragment : Fragment(R.layout.fragment_channel_videos) , VideoAdapter.VideoViewClick {
    private lateinit var grainAnimation: AnimationDrawable
    private lateinit var channelVideosRecyclerView: RecyclerView
    private lateinit var videoAdapter:VideoAdapter
    private lateinit var currentListOfVideos:List<Video>
    private var channelHandle:String?=null
    private var isLoading=false



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val motionLayout:MotionLayout = view.findViewById(R.id.channel_videos_constraint_layout)
        channelVideosRecyclerView= view.findViewById(R.id.channel_videos_recycler_view)
        val sortSpinner:Spinner=view.findViewById(R.id.sort_spinner)
        val searchImageView:ImageButton=view.findViewById(R.id.search_image)
        val searchEditText:EditText=view.findViewById(R.id.searchView)
        val searchBackImageView:ImageButton=view.findViewById(R.id.search_back)


        //get channel handle from fragment arguments
        channelHandle=arguments?.getString(ARG_CHANNEL_HANDLE)

        //set up recycler view layout manager
        channelVideosRecyclerView.layoutManager=LinearLayoutManager(context)

        //set up sort by spinner

        val sortArray:Array<String> = arrayOf("Recent","Most popular","Duration")

        sortSpinner.adapter= context?.let { context->
            ArrayAdapter(context, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,sortArray)
        }

        //get videos from api and set them up to recycler view based on selected sort by method in spinner (default is recent as page loads)
        sortSpinner.onItemSelectedListener= object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when(p2){
                    0->loadVideos("-createdAt", view)
                    1->loadVideos("-views",view)
                    2->loadVideos("-duration",view)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }



        //set fragment background animation and start it
        motionLayout.apply {
            setBackgroundResource(R.drawable.grain_animation)
            grainAnimation= background as AnimationDrawable
        }
        grainAnimation.start()




        //retrieve more videos from api if scrolled down far enough in recycler view
        channelVideosRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                val linearLayoutManager=recyclerView.layoutManager as LinearLayoutManager
                if(linearLayoutManager.findLastVisibleItemPosition()==currentListOfVideos.size-20){

                    if(!isLoading) {

                        loadMore()
                        isLoading=true
                    }

                }
            }
        })

        //toggle search view
        searchImageView.setOnClickListener {

            //show edit text and back button
            searchEditText.visibility = View.VISIBLE
            searchBackImageView.visibility = View.VISIBLE

            //hide spinner and search button
            sortSpinner.visibility = View.INVISIBLE
            it.visibility=View.INVISIBLE

            //focus on search edit text and show keyboard
            searchEditText.requestFocus()
            val input:InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            input.showSoftInput(searchEditText,0)



        }

        searchBackImageView.setOnClickListener {

            //hide search edit text and back button
            searchEditText.visibility=View.GONE
            it.visibility=View.GONE

            //show spinner and search button
            sortSpinner.visibility=View.VISIBLE
            searchImageView.visibility=View.VISIBLE

            //clear search edit text
            searchEditText.setText("")

            //reset search results and view default videos
            //if spinner is on "recent" just load videos
            if(sortSpinner.selectedItemPosition ==0){
                loadVideos(view = view)
            }
            else{ //set spinner on "recent" which also loads videos
                sortSpinner.setSelection(0)
            }


            //hide keyboard
            val input:InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            input.hideSoftInputFromWindow(searchEditText.windowToken,0)
        }

        //search edit text listener
        searchEditText.setOnEditorActionListener { _, i, _ ->
            if (searchEditText.text.isNotEmpty()) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    searchChannel(channelHandle, searchEditText.text.toString())
                }
            }

            //hide keyboard
            val input:InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            input.hideSoftInputFromWindow(searchEditText.windowToken,0)

        }




    }
    // retrieve videos from api based on current list size as a page start point
    private fun loadMore(){
        channelHandle?.let { ChikiFetcher().fetchVideosOfaChannel(it,currentListOfVideos.size).observe(viewLifecycleOwner
        ) { list ->
            currentListOfVideos =
                currentListOfVideos + list //add lists to get all available videos size
            videoAdapter.submitList(currentListOfVideos) //load new videos in recyclerview
            isLoading = false
        }
        }
    }

    //get videos from api and set them up to recycler view
    private fun loadVideos(sort:String="-createdAt",view: View){
        val noVideosTextview:TextView=view.findViewById(R.id.no_channel_videos_found_text_view)
        val progressBar:ProgressBar = view.findViewById(R.id.progressBar)

        //show progress bar if sort by was changed
        if(progressBar.visibility==View.GONE){
            videoAdapter.submitList(emptyList()) // empty recycler view as the video list will change
            progressBar.visibility=View.VISIBLE
        }

        channelHandle?.let { ChikiFetcher().fetchVideosOfaChannel(it,sortBy = sort).observe(viewLifecycleOwner
        ) { list ->
            //apply recycler view adapter with retrieved list
            channelVideosRecyclerView.apply {
                videoAdapter = VideoAdapter()
                videoAdapter.submitList(list)
                videoAdapter.setVideoViewClickListener(this@ChannelVideosFragment)
                adapter = videoAdapter
            }

            currentListOfVideos = list

            //if there are no videos for the channel show text view
            if (list.isEmpty()) {
                noVideosTextview.visibility = View.VISIBLE
            }
            //hide loading bar after loading list
            progressBar.visibility = View.GONE
        }
        }
    }

    private fun searchChannel(channelHandle: String?,searchText:String){
        val progressBar=view?.findViewById<ProgressBar>(R.id.progressBar)
        val tempList:MutableList<Video> = mutableListOf()

        //show loading progress bar while getting the search results
        if(progressBar?.visibility==View.GONE){
            videoAdapter.submitList(emptyList()) // empty recycler view as the video list will change
            progressBar.visibility=View.VISIBLE
        }


        ChikiFetcher().searchForVideos(searchText).observe(viewLifecycleOwner) { list ->

            //get all videos from the current channel
            list.forEach {
                if (it.videoChannel.channelHandle == channelHandle) {
                    tempList.add(it)
                }
            }

            //update recycler view adapter with the new list
            videoAdapter.submitList(tempList)
            //remove progress bar after loading
            progressBar?.visibility = View.GONE
        }

    }


    companion object {
        //returns a bundle based on arg_channel_id string key
        fun getVideoArgsBundle(channelHandle: String): Bundle {
            return bundleOf(ARG_CHANNEL_HANDLE to channelHandle)
        }


    }

    override fun onVideoClick(videoId:UUID,videoName:String,videoDescription:String) {


        //put video container above channel videos recycler view
        //view?.findViewById<ConstraintLayout>(R.id.video_container)?.bringToFront()


        parentFragmentManager.beginTransaction().apply {
            replace(R.id.video_container,VideoPlayerFragment.newInstance(videoId,videoName,videoDescription))
            commit()
        }
    }


}