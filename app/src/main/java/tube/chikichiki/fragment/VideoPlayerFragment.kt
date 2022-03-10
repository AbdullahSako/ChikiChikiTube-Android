package tube.chikichiki.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewParent
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.StyledPlayerControlView
import com.google.android.exoplayer2.ui.StyledPlayerView
import tube.chikichiki.R
import tube.chikichiki.activity.EXTRA_PLAYBACK_POSITION
import tube.chikichiki.activity.FullScreenVideoActivity
import tube.chikichiki.api.ChikiFetcher
import tube.chikichiki.view.CustomExoPlayerView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs


private const val ARG_VIDEO_ID: String = "VIDEOID"
private const val ARG_VIDEO_NAME: String = "VIDEONAME"
private const val ARG_VIDEO_DESCRIPTION: String = "VIDEODESCRIPTION"
class VideoPlayerFragment : Fragment(R.layout.fragment_video_player) {

    private var videoPlayer: ExoPlayer? = null
    private var playlistUrl:String?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val motionLayout: MotionLayout = view.findViewById(R.id.video_player_motion_layout)
        val closeFragment: ImageButton = view.findViewById(R.id.close_video_icon)
        val pauseOrPlayVideoBtn: ImageButton = view.findViewById(R.id.pause_video_image_view)
        val videoTitle: TextView = view.findViewById(R.id.video_title_text_view)
        val videoFullTitle: TextView = view.findViewById(R.id.video_title_full)
        val openDescriptionBtn: ConstraintLayout =
            view.findViewById(R.id.description_open_container_clickable)
        val descriptionCloseBtn: ImageButton = view.findViewById(R.id.description_close_button)
        val descriptionContainer: MotionLayout = view.findViewById(R.id.description_motion_layout)
        val descriptionVideoTitle: TextView = view.findViewById(R.id.description_video_title)
        val descriptionText: TextView = view.findViewById(R.id.description_text)
        val viewsText:TextView=view.findViewById(R.id.video_views)
        val videoPublishedAt:TextView=view.findViewById(R.id.published_at_date)
        val videoPlayerView=view.findViewById<StyledPlayerView>(R.id.video_player)
        val exoPlayerProgressBar:ProgressBar=videoPlayerView.findViewById(R.id.exo_player_progress_bar)
        videoPlayer = context?.let {
            ExoPlayer.Builder(it).setSeekBackIncrementMs(10000).setSeekForwardIncrementMs(10000)
                .build()
        }



        //get argument
        val videoId = arguments?.get(ARG_VIDEO_ID) as UUID
        val videoName = arguments?.get(ARG_VIDEO_NAME) as String
        val videoDescription = arguments?.get(ARG_VIDEO_DESCRIPTION) as String

        //bind exoplayer to view
        videoPlayerView.player = videoPlayer


        //set video details/description
        videoFullTitle.text = videoName
        videoTitle.text = videoName
        descriptionVideoTitle.text = videoName
        descriptionText.text = videoDescription

        //get playlist url
        //using playlist url on exoplayer gets correct video duration
        ChikiFetcher().fetchStreamingPlaylist(videoId)
            .observe(viewLifecycleOwner) {
                //fill views and published date text views
                viewsText.text = getString(R.string.views, it[0].views)
                videoPublishedAt.text = getFormattedDate(it[0].publishedAt)

                playlistUrl = it[0].playlistUrl

                //initialize video file and play on video player
                val media: MediaItem = MediaItem.Builder().setUri(it[0].playlistUrl).build()
                videoPlayer?.addMediaItem(media)
                videoPlayer?.prepare()
                videoPlayer?.play()

            }


        //set close video image view on click listener
        closeFragment.setOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }


        //open description button listener
        openDescriptionBtn.setOnClickListener {
            descriptionContainer.bringToFront() //bring container over video title and description arrow
            descriptionContainer.transitionToEnd()
        }

        //close description button listener
        descriptionCloseBtn.setOnClickListener {
            openDescriptionBtn.bringToFront() //bring title and description arrow button to front when description container closes
            descriptionContainer.transitionToStart()
        }


        //setup motion layout listener
        setUpMotionLayoutListener(motionLayout)

        //setup video player listeners
        exoPlayerCustomControllerViewSetUp(view)

        //pause / play button
        pauseOrPlayVideoBtn.setOnClickListener {

            if (videoPlayer?.isPlaying == true) {
                videoPlayer?.pause()
            } else if (videoPlayer?.isPlaying == false) {
                videoPlayer?.prepare()
                videoPlayer?.play()

            }
        }


        //exoplayer (video player) listener TODO: REMOVE WHEN DONE
        videoPlayer?.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                Log.d("TESTLOG", error.toString())
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if(playbackState== Player.STATE_BUFFERING){
                    exoPlayerProgressBar.visibility=View.VISIBLE
                }
                else if(playbackState == Player.STATE_READY){
                    exoPlayerProgressBar.visibility=View.INVISIBLE
                }
            }



            //change minimized pause/play image based on player
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                if (isPlaying) {
                    pauseOrPlayVideoBtn.setImageDrawable(context?.let {
                        getDrawable(
                            it,
                            R.drawable.ic_pause
                        )
                    })
                } else {
                    pauseOrPlayVideoBtn.setImageDrawable(context?.let {
                        getDrawable(
                            it, R.drawable.ic_play
                        )
                    })
                }
            }

        })


    }

    //returns date as "Time Ago" for example: 2 days ago
    private fun getFormattedDate(publishedAt: String?): String {

        val simpleDateFormat= SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())

        val date=simpleDateFormat.parse(publishedAt)

        return "- "+TimeAgo.using(date.time)
    }


    private fun setUpMotionLayoutListener(motionLayout: MotionLayout) {

        val channelActivityMotionLayout =
            activity?.findViewById<MotionLayout>(R.id.channel_activity_motion_layout)
        val mainActivityMotionLayout =
            activity?.findViewById<MotionLayout>(R.id.activity_main_motion_layout)
        val videoPlayerView:StyledPlayerView?=view?.findViewById(R.id.video_player)


        motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
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
                channelActivityMotionLayout?.progress = (1.0f - abs(progress))

                //in case the video is opened from a main activity - this hides/shows the toolbar and bottom nav bar based on whether the video is opened or minimized
                mainActivityMotionLayout?.progress = (1.0f - abs(progress))


                //remove video player control buttons
                if(progress>0.1f) {
                    if(videoPlayerView?.useController==true) {
                        videoPlayerView.useController = false
                        videoPlayerView.hideController()
                    }
                }

                //showing video player controls at start state glitches height and width of the control view
                //this ensures the control view is shown correctly
                //show video player control button
                if(progress<0.1f){
                    if(videoPlayerView?.useController==false) {
                        videoPlayerView.useController = true
                        videoPlayerView.showController()
                    }
                }


            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                //for some reason main activity motion layout does not stay at start state which causes recycler view to not appear
                //this progresses main activity to start state
                if (currentId == motionLayout?.endState) {
                    mainActivityMotionLayout?.transitionToStart()
                }

                // double check if video player controller got enabled in onTransitionChange
                if(currentId ==motionLayout?.startState){
                    if(videoPlayerView?.useController==false){
                        videoPlayerView.useController=true
                        videoPlayerView.showController()
                    }
                }

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

    private fun exoPlayerCustomControllerViewSetUp(view: View) {
        val playerView= view.findViewById<CustomExoPlayerView>(R.id.video_player)
        val play: ImageButton = playerView.findViewById(R.id.control_view_play_btn)
        val forward: ImageButton = playerView.findViewById(R.id.control_view_forward_btn)
        val replay: ImageButton = playerView.findViewById(R.id.control_view_replay_btn)
        val fullscreen: ImageButton = playerView.findViewById(R.id.control_view_fullscreen_btn)
        val timeBar: DefaultTimeBar = playerView.findViewById(R.id.exo_progress)

        //play / pause button on click listener
        play.setOnClickListener {
            if (videoPlayer?.isPlaying == true) {
                videoPlayer?.pause()
                play.setImageDrawable(context?.let { it1 ->
                    getDrawable(
                        it1,
                        R.drawable.ic_play_circle
                    )
                })
            } else {
                videoPlayer?.prepare()
                videoPlayer?.play()
                play.setImageDrawable(context?.let { it1 ->
                    getDrawable(
                        it1,
                        R.drawable.ic_pause_circle
                    )
                })
            }
        }
        //seek forward 10 seconds
        forward.setOnClickListener {
            videoPlayer?.seekForward()
        }
        //seek back 10 seconds
        replay.setOnClickListener {
            videoPlayer?.seekBack()
        }

        //seek bar colors
        timeBar.setScrubberColor(ContextCompat.getColor(requireContext(), R.color.orange))
        timeBar.setPlayedColor(ContextCompat.getColor(requireContext(), R.color.orange))

        //full screen button
        fullscreen.setOnClickListener {
            val intent=FullScreenVideoActivity.newInstance(activity,videoPlayer?.currentPosition,videoPlayer?.playWhenReady,playlistUrl)
            videoPlayer?.stop()
            resultLauncher.launch(intent)
        }


    }

    //set playback position result sent back by fullscreen activity
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result->
        if(result.resultCode== Activity.RESULT_OK){

            val data: Intent?=result.data
            data?.extras?.getLong(EXTRA_PLAYBACK_POSITION)?.let {
                videoPlayer?.seekTo(
                    it
                )
            }
            videoPlayer?.prepare()


        }
    }




    override fun onStart() {
        super.onStart()


        //hides playlists/videos tab layout in channel activity by progressing motion layout
        activity?.findViewById<MotionLayout>(R.id.channel_activity_motion_layout)?.transitionToEnd()

        //when a video is up viewpager in channel activity is disabled since it affects motion layout
        activity?.findViewById<ViewPager2>(R.id.pager)?.isUserInputEnabled = false
    }

    override fun onStop() {
        super.onStop()

        //enable viewpager when video closes
        activity?.findViewById<ViewPager2>(R.id.pager)?.isUserInputEnabled = true

        videoPlayer?.stop()
    }



    override fun onDestroy() {
        super.onDestroy()

        videoPlayer?.release()
        Log.d("TESTLOG", "VIDEO PLAYER DESTROYED")
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)

        //minimize/close video on back press
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val videoMotionLayout: MotionLayout? =
                    view?.findViewById(R.id.video_player_motion_layout)
                //if video minimized close it else minimize it
                if (videoMotionLayout?.currentState == videoMotionLayout?.endState) {
                    parentFragmentManager.beginTransaction().remove(this@VideoPlayerFragment)
                        .commit()
                } else {
                    videoMotionLayout?.transitionToEnd()
                }


            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }





    companion object {
        fun newInstance(
            videoId:UUID,videoName:String,videoDescription:String
        ): VideoPlayerFragment {
            return VideoPlayerFragment().apply {
                arguments = bundleOf(
                    ARG_VIDEO_ID to videoId, ARG_VIDEO_NAME to videoName, ARG_VIDEO_DESCRIPTION to videoDescription
                )

            }
        }
    }
}



