package tube.chikichiki.sako.fragment

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tube.chikichiki.sako.R
import tube.chikichiki.sako.Utils
import tube.chikichiki.sako.adapter.VideoAdapter
import tube.chikichiki.sako.database.ChikiChikiDatabaseRepository
import tube.chikichiki.sako.viewModel.RecentVideosViewModel
import java.util.*


class RecentVideosFragment : Fragment(R.layout.fragment_recent_videos),
    VideoAdapter.VideoViewClick {
    private lateinit var grainAnimation: AnimationDrawable
    private var recentVideosViewModel: RecentVideosViewModel? = null
    private lateinit var recentRecyclerView: RecyclerView
    private lateinit var videoAdapter: VideoAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recentVideosViewModel =
            activity?.let { ViewModelProvider(it).get(RecentVideosViewModel::class.java) }


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressbar: ProgressBar = view.findViewById(R.id.progressBar)
        val constraint: ConstraintLayout = view.findViewById(R.id.recent_constraint_layout)
        recentRecyclerView = view.findViewById(R.id.recent_videos_recycler_view)


        //set recycler view layout manager and adapter
        recentRecyclerView.layoutManager = LinearLayoutManager(context)
        videoAdapter = VideoAdapter()
        recentRecyclerView.adapter = videoAdapter

        //set fragment background animation and start it
        constraint.apply {
            setBackgroundResource(R.drawable.grain_animation)
            grainAnimation = background as AnimationDrawable
        }
        grainAnimation.start()


        //retrieve video list from api
        recentVideosViewModel?.recentVideosLiveData?.observe(viewLifecycleOwner) { videos ->
            ChikiChikiDatabaseRepository.get().getAllWatchedVideos().observe(viewLifecycleOwner) {


                videoAdapter.submitList(Utils.getPairOfVideos(videos, it))
                videoAdapter.setVideoViewClickListener(this)


                //remove progressbar after loading video list
                progressbar.visibility = View.GONE

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

        //open Video Player Fragment
        parentFragmentManager.beginTransaction().apply {
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