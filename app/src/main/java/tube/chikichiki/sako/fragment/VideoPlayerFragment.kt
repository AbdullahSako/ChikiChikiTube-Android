package tube.chikichiki.sako.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaItem.SubtitleConfiguration
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.material.snackbar.Snackbar
import tube.chikichiki.sako.R
import tube.chikichiki.sako.Utils
import tube.chikichiki.sako.activity.EXTRA_PLAYBACK_POSITION
import tube.chikichiki.sako.activity.EXTRA_PLAY_WHEN_READY_BACK
import tube.chikichiki.sako.activity.FullScreenVideoActivity
import tube.chikichiki.sako.adapter.VideoAdapter
import tube.chikichiki.sako.api.ChikiFetcher
import tube.chikichiki.sako.database.ChikiChikiDatabaseRepository
import tube.chikichiki.sako.model.*
import tube.chikichiki.sako.view.CustomExoPlayerView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs


private const val ARG_VIDEO_ID: String = "VIDEOID"
private const val ARG_VIDEO_NAME: String = "VIDEONAME"
private const val ARG_VIDEO_DESCRIPTION: String = "VIDEODESCRIPTION"
private const val ARG_VIDEO_THUMBNAIL: String = "VIDEOTHUMBNAIL"
private const val ARG_VIDEO_DURATION: String = "VIDEODURATION"

class VideoPlayerFragment : Fragment(R.layout.fragment_video_player_container),
    VideoAdapter.VideoViewClick {

    private var videoPlayer: ExoPlayer? = null
    private var playlistUrl: String? = null
    private lateinit var playlistVideosRecyclerView: RecyclerView
    private lateinit var playlistVideosAdapter: VideoAdapter
    private lateinit var videoId: UUID
    private lateinit var videoName: String
    private var videoDuration: Int = 0
    private lateinit var videoDescription: String
    private lateinit var videoThumbnailPath: String
    private var videoCaption: Caption? = null
    private var resultBack = false
    private lateinit var onBackPressCallback: OnBackPressedCallback
    private val chikiFetcher: ChikiFetcher by lazy { ChikiFetcher() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val motionLayout: MotionLayout = view.findViewById(R.id.video_player_motion_layout)
        val closeFragment: ImageButton = view.findViewById(R.id.close_video_icon)
        val pauseOrPlayVideoBtn: ImageButton = view.findViewById(R.id.pause_video_image_view)
        val videoTitle: TextView = view.findViewById(R.id.video_title_text_view)
        val videoFullTitle: TextView = view.findViewById(R.id.video_title_full)
        val openDescriptionBtn: ConstraintLayout =
            view.findViewById(R.id.description_open_container_clickable)
        val viewsText: TextView = view.findViewById(R.id.video_views)
        val videoPublishedAt: TextView = view.findViewById(R.id.published_at_date)
        playlistVideosRecyclerView =
            view.findViewById(R.id.video_player_playlist_videos_recycler_view)
        val videoPlayerView = view.findViewById<StyledPlayerView>(R.id.video_player)



        videoPlayer = context?.let {
            ExoPlayer.Builder(it).setSeekBackIncrementMs(10000).setSeekForwardIncrementMs(10000)
                .build()
        }

        //get argument
        videoId = arguments?.get(ARG_VIDEO_ID) as UUID
        videoName = arguments?.get(ARG_VIDEO_NAME) as String
        videoThumbnailPath = arguments?.get(ARG_VIDEO_THUMBNAIL) as String
        videoDuration = arguments?.get(ARG_VIDEO_DURATION) as Int
        videoDescription = arguments?.get(ARG_VIDEO_DESCRIPTION) as String

        //set controller hide time
        videoPlayerView.controllerShowTimeoutMs = 3000

        //bind exoplayer to view
        videoPlayerView.player = videoPlayer


        //set up recycler view layout manager
        playlistVideosRecyclerView.layoutManager = LinearLayoutManager(context)

        //set video details/description
        videoFullTitle.text = videoName
        videoTitle.text = videoName

        //get playlist url
        //using playlist url on exoplayer gets correct video duration
        chikiFetcher.fetchStreamingPlaylist(videoId)
            .observe(viewLifecycleOwner) { vidList ->

                chikiFetcher.fetchCaptions(videoId).observe(viewLifecycleOwner) { caption ->


                    //init caption
                    val subtitles = arrayListOf<SubtitleConfiguration>()
                    if (caption?.isNotEmpty() == true) {

                        videoCaption = caption[0]
                        val uri = Uri.parse("https://vtr.chikichiki.tube" + caption[0].captionPath)
                        subtitles.add(
                            SubtitleConfiguration.Builder(uri)
                                .setMimeType(MimeTypes.TEXT_VTT)
                                .setLanguage("en")
                                .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                                .build()
                        )

                    }

                    //fill views and published date text views
                    viewsText.text = getString(R.string.views, vidList[0].views)
                    videoPublishedAt.text = getFormattedDate(vidList[0].publishedAt)

                    playlistUrl = vidList[0].playlistUrl

                    //check if pip mode is on
                    if (!Utils.IsInPipMode) {


                        //initialize video file and play on video player
                        val media: MediaItem = MediaItem.Builder().setUri(vidList[0].playlistUrl)
                            .setSubtitleConfigurations(subtitles).build()

                        videoPlayer?.addMediaItem(media)
                        videoPlayer?.prepare()

                        //enable full screen button
                        videoPlayerView.findViewById<ImageButton>(R.id.control_view_fullscreen_btn).isEnabled =
                            true


                        //seek video to save user watch time
                        ChikiChikiDatabaseRepository.get().getWatchedVideo(videoId)
                            .observe(viewLifecycleOwner) {
                                if (it != null && it.watchedVideoTimeInMil != videoPlayer?.contentDuration) {

                                    videoPlayer?.seekTo(it.watchedVideoTimeInMil)

                                }
                            }

                        videoPlayer?.play()


                    } else {
                        Toast.makeText(activity, "Picture In Picture Mode On", Toast.LENGTH_SHORT)
                            .show()
                        hideControls(view)
                    }

                }
            }


        //set up recycler view by getting this video's playlist videos from api
        chikiFetcher.fetchPlaylists().observe(viewLifecycleOwner) {
            setUpPlaylistVideosRecyclerView(it, videoName)
        }


        //set close video image view on click listener (minimized)
        closeFragment.setOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }


        //open description button listener
        openDescriptionBtn.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                replace(
                    R.id.video_title_fragment_container,
                    DescriptionFragment.newInstance(videoName, videoDescription)
                )
                commit()
            }
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


        //exoplayer (video player) listener
        setupExoPlayerListener(view, videoPlayerView)


        //Add video to history
        ChikiChikiDatabaseRepository.get().addToHistory(
            HistoryVideoInfo(
                videoId,
                videoName,
                videoDescription,
                videoThumbnailPath,
                videoDuration,
                Date()
            )
        )

        //setup watch later button
        setUpWatchLater(
            videoPlayerView,
            videoId,
            videoName,
            videoDescription,
            videoThumbnailPath,
            videoDuration
        )


    }


    //searches through all playlists based on video name and gets video's playlist videos
    private fun setUpPlaylistVideosRecyclerView(playlists: List<VideoPlaylist>, videoName: String) {
        var playlist: VideoPlaylist? = null
        val progressBar: ProgressBar? = view?.findViewById(R.id.progressBar)
        val noPlaylistsText: TextView? = view?.findViewById(R.id.no_playlist_for_video_text)


        //get playlist of a video
        playlists.forEach {
            if (videoName.contains(it.displayName)) {
                playlist = it
            }
        }


        if (playlist != null) {

            //get videos of the playlist
            playlist?.id?.let { it ->
                chikiFetcher.fetchVideosOfaPlaylist(it).observe(viewLifecycleOwner) { videoList ->

                    ChikiChikiDatabaseRepository.get().getAllWatchedVideos()
                        .observe(viewLifecycleOwner) {

                            //filter out current video
                            val temp: List<Video> = videoList.filter { it.uuid != videoId }

                            //set up recycler view adapter
                            playlistVideosAdapter = VideoAdapter()
                            playlistVideosAdapter.submitList(Utils.getPairOfVideos(temp, it))
                            playlistVideosAdapter.setVideoViewClickListener(this)
                            playlistVideosRecyclerView.adapter = playlistVideosAdapter


                            //hide progress bar after loading
                            progressBar?.visibility = View.GONE

                        }

                }
            }
        } else {
            //hide progress bar if there is no playlist
            progressBar?.visibility = View.GONE

            noPlaylistsText?.visibility = View.VISIBLE


        }


    }

    //returns date as "Time Ago" for example: 2 days ago
    private fun getFormattedDate(publishedAt: String?): String {

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())

        val date = simpleDateFormat.parse(publishedAt)

        return "- " + TimeAgo.using(date.time)
    }


    private fun setUpMotionLayoutListener(motionLayout: MotionLayout) {

        val channelActivityMotionLayout =
            activity?.findViewById<MotionLayout>(R.id.channel_fragment_motion_layout)
        val mainActivityMotionLayout =
            activity?.findViewById<MotionLayout>(R.id.activity_main_motion_layout)
        val videoPlayerView: StyledPlayerView? = view?.findViewById(R.id.video_player)



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

                if (endId != R.id.close_state) { //if its not drag down to close fragment transition
                    //in case the video is opened from a channel - this hides/shows the tab layout based on whether the video is opened or minimized
                    channelActivityMotionLayout?.progress = (1.0f - abs(progress))

                    //in case the video is opened from a main activity - this hides/shows the toolbar and bottom nav bar based on whether the video is opened or minimized
                    mainActivityMotionLayout?.progress = (1.0f - abs(progress))

                }


                //remove video player control buttons
                if (progress > 0.1f) {
                    if (videoPlayerView?.useController == true) {
                        videoPlayerView.useController = false
                        videoPlayerView.hideController()
                    }
                }

                //showing video player controls at start state glitches height and width of the control view
                //this ensures the control view is shown correctly
                //show video player control button
                if (progress < 0.1f) {
                    if (videoPlayerView?.useController == false) {
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
                    channelActivityMotionLayout?.transitionToStart()

                    //remove on back press when minimized so the user can navigate back between fragments
                    onBackPressCallback.remove()
                }

                // double check if video player controller got enabled in onTransitionChange
                if (currentId == R.id.videoPlayerStart) {

                    //since progressing both the motionlayouts in on transition change doesnt progress them fully to transition end/start
                    //i had to use the following codes to avoid bugs in the ui
                    //hides playlists/videos tab layout in channel activity by progressing motion layout
                    channelActivityMotionLayout?.transitionToEnd()
                    //hides toolbar in main activity by progressing motion layout
                    mainActivityMotionLayout?.transitionToEnd()

                    if (videoPlayerView?.useController == false) {
                        videoPlayerView.useController = true
                        videoPlayerView.showController()
                    }
                    addBackPressCallBack()
                }

                //remove video player fragment when video player is dragged down
                if (currentId == R.id.close_state) {
                    parentFragmentManager.beginTransaction().remove(this@VideoPlayerFragment)
                        .commit()
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
        val playerView = view.findViewById<CustomExoPlayerView>(R.id.video_player)
        val play: ImageButton = playerView.findViewById(R.id.control_view_play_btn)
        val forward: ImageButton = playerView.findViewById(R.id.control_view_forward_btn)
        val replay: ImageButton = playerView.findViewById(R.id.control_view_replay_btn)
        val fullscreen: ImageButton = playerView.findViewById(R.id.control_view_fullscreen_btn)
        val timeBar: DefaultTimeBar = playerView.findViewById(R.id.exo_progress)

        //play / pause button on click listener
        play.setOnClickListener {
            if (videoPlayer?.isPlaying == true) {
                videoPlayer?.pause()

            } else {
                videoPlayer?.prepare()
                videoPlayer?.play()
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
        timeBar.setPlayedColor(ContextCompat.getColor(requireContext(), R.color.icon_yellow))

        fullscreen.isEnabled = false
        //full screen button
        fullscreen.setOnClickListener {
            val intent = FullScreenVideoActivity.newInstance(
                activity,
                videoPlayer?.currentPosition,
                videoPlayer?.playWhenReady,
                playlistUrl,
                videoName,
                videoId,
                videoDescription,
                videoDuration,
                videoThumbnailPath,
                videoCaption
            )
            videoPlayer?.stop()
            resultLauncher.launch(intent)
        }


    }

    private fun setupExoPlayerListener(view: View, videoPlayerView: View) {

        val exoPlayerProgressBar: ProgressBar =
            videoPlayerView.findViewById(R.id.exo_player_progress_bar)
        val controlPlayBtn: ImageButton = videoPlayerView.findViewById(R.id.control_view_play_btn)
        val pauseOrPlayVideoBtn: ImageButton = view.findViewById(R.id.pause_video_image_view)


        videoPlayer?.addListener(object : Player.Listener {


            //hide progress bar when not buffering
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if (playbackState == Player.STATE_BUFFERING) {
                    exoPlayerProgressBar.visibility = View.VISIBLE
                } else if (playbackState == Player.STATE_READY) {
                    exoPlayerProgressBar.visibility = View.INVISIBLE
                }
            }


            //change minimized pause/play image based on player
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                if (isPlaying) {

                    //change minimized play button image to paused image
                    pauseOrPlayVideoBtn.setImageDrawable(context?.let {
                        getDrawable(
                            it,
                            R.drawable.ic_pause
                        )
                    })
                    //change player control view play button image to paused image
                    controlPlayBtn.setImageDrawable(context?.let { it1 ->
                        getDrawable(
                            it1,
                            R.drawable.ic_pause_circle
                        )
                    })


                } else {
                    //change minimized pause button image to play image
                    pauseOrPlayVideoBtn.setImageDrawable(context?.let {
                        getDrawable(
                            it, R.drawable.ic_play
                        )
                    })
                    //change player control view pause button image to play image
                    controlPlayBtn.setImageDrawable(context?.let { it1 ->
                        getDrawable(
                            it1,
                            R.drawable.ic_play_circle
                        )
                    })
                }
            }

        })
    }

    private fun setUpWatchLater(
        videoPlayerView: View,
        videoId: UUID,
        videoName: String,
        videoDescription: String,
        videoThumbnailPath: String,
        videoDuration: Int
    ) {
        val watchLaterBtn: ImageButton = videoPlayerView.findViewById(R.id.control_view_watchlater)
        val rootView = view?.findViewById<ConstraintLayout>(R.id.video_player_motion_layout)

        ChikiChikiDatabaseRepository.get().getWatchLaterItem(videoId)
            .observe(viewLifecycleOwner) { watchLaterItem ->

                if (watchLaterItem == null) { //if not added yet
                    watchLaterBtn.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.ic_add_to_watchlater
                        )
                    )

                    watchLaterBtn.setOnClickListener {
                        ChikiChikiDatabaseRepository.get().addToWatchLater(
                            WatchLater(
                                videoId,
                                videoName,
                                videoDescription,
                                videoThumbnailPath,
                                videoDuration,
                                Date()
                            )
                        )

                        if (rootView != null) {
                            val snack = Snackbar.make(
                                rootView,
                                R.string.video_added_watch_later,
                                Snackbar.LENGTH_SHORT
                            ).setAnchorView(R.id.bottomNavigationView).setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.font_pink
                                )
                            ).setBackgroundTint(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.dark_grey
                                )
                            )

                            //change snack bar text font
                            snack.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).typeface =
                                ResourcesCompat.getFont(requireActivity(), R.font.mochiypoppone)
                            snack.show()
                        }
                    }
                } else { //if already added to watch later
                    watchLaterBtn.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.ic_added_to_watchlater
                        )
                    )

                    watchLaterBtn.setOnClickListener {
                        ChikiChikiDatabaseRepository.get().removeFromWatchLater(watchLaterItem)

                        if (rootView != null) {
                            val snack = Snackbar.make(
                                rootView,
                                R.string.video_removed_watch_later,
                                Snackbar.LENGTH_SHORT
                            ).setAnchorView(R.id.bottomNavigationView).setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.font_pink
                                )
                            ).setBackgroundTint(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.dark_grey
                                )
                            )

                            //change snack bar text font
                            snack.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).typeface =
                                ResourcesCompat.getFont(requireActivity(), R.font.mochiypoppone)
                            snack.show()
                        }

                    }

                }

            }

    }

    //hide controls of the player
    private fun hideControls(view: View) {
        val playerView = view.findViewById<CustomExoPlayerView>(R.id.video_player)
        val play: ImageButton = playerView.findViewById(R.id.control_view_play_btn)
        val forward: ImageButton = playerView.findViewById(R.id.control_view_forward_btn)
        val replay: ImageButton = playerView.findViewById(R.id.control_view_replay_btn)
        val fullscreen: ImageButton = playerView.findViewById(R.id.control_view_fullscreen_btn)
        val timeBar: DefaultTimeBar = playerView.findViewById(R.id.exo_progress)
        val progressBar: ProgressBar = playerView.findViewById(R.id.exo_player_progress_bar)
        val videoTitleTextView: TextView = playerView.findViewById(R.id.exo_player_view_video_title)


        play.visibility = View.INVISIBLE
        forward.visibility = View.INVISIBLE
        replay.visibility = View.INVISIBLE
        fullscreen.visibility = View.INVISIBLE
        timeBar.visibility = View.INVISIBLE
        progressBar.visibility = View.INVISIBLE
        videoTitleTextView.visibility = View.VISIBLE
        videoTitleTextView.text = "Picture In Picture Mode On!"


    }

    //set playback position result sent back by fullscreen activity
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val data: Intent? = result.data
                data?.extras?.getLong(EXTRA_PLAYBACK_POSITION)?.let {
                    videoPlayer?.seekTo(
                        it
                    )

                }
                data?.extras?.getBoolean(EXTRA_PLAY_WHEN_READY_BACK)?.let {
                    videoPlayer?.playWhenReady = it
                }
                resultBack = true
                videoPlayer?.prepare()


            }
        }


    private fun addBackPressCallBack() {
        //minimize video on back press
        onBackPressCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val videoMotionLayout: MotionLayout? =
                    view?.findViewById(R.id.video_player_motion_layout)
                //if video is not minimized minimize it
                if (videoMotionLayout?.currentState == videoMotionLayout?.startState) {
                    videoMotionLayout?.transitionToEnd()
                }

            }


        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressCallback)
    }


    override fun onStart() {
        super.onStart()

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


        //save how much the user watched if it is more than 30% or the video duration
        val current = videoPlayer?.currentPosition
        val total = videoPlayer?.contentDuration
        //default is 23:55
        val minDuration = total?.times((0.01))?.toLong() ?: (1434856).toLong()

        if (current != null) {
            if (current >= minDuration) {
                ChikiChikiDatabaseRepository.get().addWatchedVideo(WatchedVideo(videoId, current))
            }
        }

        videoPlayer?.release()
        Log.d("TESTLOG", "VIDEO PLAYER DESTROYED")
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

        addBackPressCallBack()
    }

    companion object {
        fun newInstance(
            videoId: UUID,
            videoName: String,
            videoDescription: String,
            thumbnailPreviewPath: String,
            videoDuration: Int
        ): VideoPlayerFragment {
            return VideoPlayerFragment().apply {
                arguments = bundleOf(
                    ARG_VIDEO_ID to videoId,
                    ARG_VIDEO_NAME to videoName,
                    ARG_VIDEO_DESCRIPTION to videoDescription,
                    ARG_VIDEO_THUMBNAIL to thumbnailPreviewPath,
                    ARG_VIDEO_DURATION to videoDuration
                )

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
        //FOR PLAYLIST VIDEOS UNDER THE VIDEO PLAYER

        activity?.findViewById<MotionLayout>(R.id.activity_main_motion_layout)?.transitionToEnd()

        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(
                R.id.video_container,
                newInstance(videoId, videoName, videoDescription, previewPath, duration)
            )
            commit()

        }
    }
}



