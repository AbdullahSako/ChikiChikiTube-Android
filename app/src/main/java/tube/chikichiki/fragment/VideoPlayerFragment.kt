package tube.chikichiki.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.VideoView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import tube.chikichiki.R
import tube.chikichiki.api.ChikiFetcher
import java.util.*
import kotlin.math.abs

private const val ARG_VIDEO_ID:String="VIDEOUUID"
class VideoPlayerFragment : Fragment(R.layout.fragment_video_player) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val motionLayout:MotionLayout=view.findViewById(R.id.video_player_motion_layout)
        val videoId:UUID=arguments?.get(ARG_VIDEO_ID) as UUID
        val videoPlayer:VideoView=view.findViewById(R.id.video_player)
        val closeFragment:ImageView=view.findViewById(R.id.close_video_icon)

        //parent activity view pager if available
        val pager=activity?.findViewById<ViewPager2>(R.id.pager)


        ChikiFetcher().fetchVideoFile(videoId).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            videoPlayer.setVideoPath(it[0].fileUrl)
            videoPlayer.start()
        })

        closeFragment.setOnClickListener {
            pager?.isUserInputEnabled=true
            parentFragmentManager.beginTransaction().remove(VideoPlayerFragment@this).commit()
        }

        setUpMotionLayoutListener(motionLayout)

    }


    private fun setUpMotionLayoutListener(motionLayout: MotionLayout){

        val channelActivityMotionLayout=activity?.findViewById<MotionLayout>(R.id.channel_activity_motion_layout)

        motionLayout.setTransitionListener(object : MotionLayout.TransitionListener{
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {
            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                //in case the video is opened from a channel - this hides/shows the tab layout based on whether the video is opened or minimized
                channelActivityMotionLayout?.progress= (1.0-abs(progress)).toFloat()

            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
            }

        })

    }

    companion object{
        fun newInstance(videoId:UUID):VideoPlayerFragment {
            return VideoPlayerFragment().apply {
                arguments= bundleOf(ARG_VIDEO_ID to videoId)
            }
        }
    }
}