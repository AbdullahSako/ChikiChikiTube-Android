package tube.chikichiki.sako.fragment

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ProgressBar
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tube.chikichiki.sako.R
import tube.chikichiki.sako.adapter.VideoAdapter

import tube.chikichiki.sako.viewModel.MostViewedVideosViewModel
import java.util.*


class MostViewedVideosFragment : Fragment(R.layout.fragment_most_viewed_videos),VideoAdapter.VideoViewClick {
    private lateinit var grainAnimation: AnimationDrawable
    private var mostViewedVideosViewModel: MostViewedVideosViewModel? = null
    private lateinit var mostViewedVideosRecyclerView: RecyclerView
    private lateinit var videoAdapter: VideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mostViewedVideosViewModel= activity?.let { ViewModelProvider(it).get(
            MostViewedVideosViewModel::class.java) }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressbar:ProgressBar=view.findViewById(R.id.progressBar)
        val constraint: ConstraintLayout =view.findViewById(R.id.most_view_constraint_layout)
        mostViewedVideosRecyclerView=view.findViewById(R.id.most_viewed_videos_recycler_view)

        //set recycler view layout manager
        mostViewedVideosRecyclerView.layoutManager=LinearLayoutManager(context)

        //set fragment background animation and start it
        constraint.apply {
            setBackgroundResource(R.drawable.grain_animation)
            grainAnimation= background as AnimationDrawable
        }
        grainAnimation.start()

        //retrieve video list from api
        mostViewedVideosViewModel?.mostViewedVideosLiveData?.observe(viewLifecycleOwner) {
            videoAdapter = VideoAdapter()
            videoAdapter.submitList(it)
            videoAdapter.setVideoViewClickListener(this)
            mostViewedVideosRecyclerView.adapter = videoAdapter

            //remove progressbar after loading video list
            progressbar.visibility = View.GONE
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
            setCustomAnimations(R.anim.slide_up,0)
            replace(R.id.video_container,VideoPlayerFragment.newInstance(videoId,videoName,videoDescription,previewPath,duration))
            commit()
        }


    }


}