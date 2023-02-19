package tube.chikichiki.sako.fragment

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tube.chikichiki.sako.R
import tube.chikichiki.sako.Utils
import tube.chikichiki.sako.adapter.VideoAdapter
import tube.chikichiki.sako.api.ChikiFetcher
import tube.chikichiki.sako.database.ChikiChikiDatabaseRepository
import java.util.*

private const val ARG_SEARCH_TERM = "SEARCHTERM"

class MainSearchFragment : Fragment(R.layout.fragment_main_search), VideoAdapter.VideoViewClick {
    private lateinit var grainAnimation: AnimationDrawable

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val noVideosText: TextView = view.findViewById(R.id.main_search_no_videos_found_text)
        val rootView: ConstraintLayout = view.findViewById(R.id.main_search_root_view)
        val progressbar: ProgressBar = view.findViewById(R.id.progressBar)
        val searchRecyclerView: RecyclerView = view.findViewById(R.id.main_search_recycler_view)
        val videoAdapter = VideoAdapter()

        //get arg
        val searchTerm = arguments?.getString(ARG_SEARCH_TERM)

        //set recycler view layout manager and adapter
        searchRecyclerView.layoutManager = LinearLayoutManager(context)
        searchRecyclerView.adapter = videoAdapter

        //set fragment background animation and start it
        rootView.apply {
            setBackgroundResource(R.drawable.grain_animation)
            grainAnimation = background as AnimationDrawable
        }
        grainAnimation.start()


        if (searchTerm != null) {
            ChikiFetcher().searchForVideos(searchTerm).observe(viewLifecycleOwner) { videos ->
                ChikiChikiDatabaseRepository.get().getAllWatchedVideos()
                    .observe(viewLifecycleOwner) {

                        videoAdapter.submitList(Utils.getPairOfVideos(videos, it))
                        videoAdapter.setVideoViewClickListener(this)


                        //remove progressbar after loading video list
                        progressbar.visibility = View.GONE

                        //if empty show text
                        if (videos.isEmpty()) {
                            noVideosText.visibility = View.VISIBLE
                        }
                    }
            }
        }

    }

    override fun onVideoClick(
        videoId: UUID,
        videoName: String,
        videoDescription: String,
        previewPath: String,
        duration: Int
    ) {
        //hides main activity toolbar and bottom nav bar by progressing motion layout
        activity?.findViewById<MotionLayout>(R.id.activity_main_motion_layout)?.transitionToEnd()

        //open video fragment
        parentFragmentManager.beginTransaction().apply {
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

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TESTLOG", "SEARCH DESTROYED")
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

        //remove fragment on back press instead of closing activity
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //hide activity search views
                requireActivity().apply {
                    findViewById<ImageButton>(R.id.main_activity_back_image_button).visibility =
                        View.GONE
                    findViewById<EditText>(R.id.main_activity_search_edit_text).visibility =
                        View.GONE

                    //clear search edit text
                    findViewById<EditText>(R.id.main_activity_search_edit_text).setText("")
                }

                //show activity toolbar
                requireActivity().apply {
                    findViewById<ImageButton>(R.id.main_activity_search_button).visibility =
                        View.VISIBLE
                    findViewById<ImageView>(R.id.toolbar_image).visibility = View.VISIBLE
                    findViewById<TextView>(R.id.toolbar_text).visibility = View.VISIBLE
                    findViewById<TextView>(R.id.header_text).visibility = View.VISIBLE
                }

                //remove search fragment
                parentFragmentManager.beginTransaction().remove(this@MainSearchFragment).commit()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)


    }

    companion object {
        fun newInstance(searchTerm: String): MainSearchFragment {
            val args = bundleOf(ARG_SEARCH_TERM to searchTerm)
            val fragment = MainSearchFragment()
            fragment.arguments = args
            return fragment
        }
    }
}