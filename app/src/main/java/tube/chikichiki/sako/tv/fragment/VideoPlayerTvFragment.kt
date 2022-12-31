package tube.chikichiki.sako.tv.fragment

import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.leanback.app.PlaybackSupportFragment
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.PlaybackGlue
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.lifecycle.LifecycleOwner
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import tube.chikichiki.sako.R
import tube.chikichiki.sako.api.ChikiFetcher
import tube.chikichiki.sako.database.ChikiChikiDatabaseRepository
import tube.chikichiki.sako.model.HistoryVideoInfo
import tube.chikichiki.sako.model.WatchLater
import tube.chikichiki.sako.model.WatchedVideo
import tube.chikichiki.sako.tv.other.VideoPlayerTvGlue
import java.util.*

private const val ARG_VIDEO_ID = "VIDEOID"
private const val ARG_VIDEO_TITLE = "VIDEOTITLE"
private const val ARG_VIDEO_DESC = "VIDEODESC"
private const val ARG_VIDEO_DURATION = "VIDEODURATION"
private const val ARG_VIDEO_PREV = "VIDEOPREV"

class VideoPlayerTvFragment : VideoSupportFragment(),VideoPlayerTvGlue.ActionClick {

    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector
    private lateinit var videoId: UUID
    private lateinit var videoTitle: String
    private lateinit var videoDescription: String
    private var videoDuration: Int=0
    private lateinit var videoPreviewPath: String
    private lateinit var watchLaterAction:Action
    private lateinit var playerGlue: VideoPlayerTvGlue
    private var isInWatchLater:Boolean = false
    private lateinit var watchLaterItem:WatchLater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        backgroundType = PlaybackSupportFragment.BG_NONE
        player = ExoPlayer.Builder(requireActivity()).setSeekForwardIncrementMs(10000)
            .setSeekBackIncrementMs(10000).build()
        mediaSession = MediaSessionCompat(requireContext(), getString(R.string.app_name))
        mediaSessionConnector = MediaSessionConnector(mediaSession)

        videoId = UUID.fromString(arguments?.getString(ARG_VIDEO_ID))
        videoTitle = arguments?.getString(ARG_VIDEO_TITLE) ?: ""
        videoDescription = arguments?.getString(ARG_VIDEO_DESC) ?: ""
        videoDuration = arguments?.getInt(ARG_VIDEO_DURATION) ?: 0
        videoPreviewPath = arguments?.getString(ARG_VIDEO_PREV) ?: ""


        setupWatchLaterAction()

        setupPlayer()

        //add to history
        ChikiChikiDatabaseRepository.get().addToHistory(HistoryVideoInfo(videoId,videoTitle,videoDescription,videoPreviewPath,videoDuration,Date()))


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.black))


    }

    private fun setupMediaSession() {
        //TODO MEDIA SESSION
        mediaSession.setCallback(object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                super.onPlay()
                Log.d("TESTLOG", "PLAY")
            }

        })

    }

    private fun setupWatchLaterAction(){

        //initialize watch later action
        watchLaterAction = Action(7,getString(R.string.watch_later),null,ContextCompat.getDrawable(requireContext(),R.drawable.ic_add_to_watchlater))

        ChikiChikiDatabaseRepository.get().getWatchLaterItem(videoId).observe(this){
            if(it == null){
                watchLaterAction.icon = ContextCompat.getDrawable(requireActivity(),R.drawable.ic_add_to_watchlater)
                playerGlue.notifyAdapterActionChanged()
                isInWatchLater = false
            }
            else{
                watchLaterAction.icon = ContextCompat.getDrawable(requireActivity(),R.drawable.ic_added_to_watchlater)
                playerGlue.notifyAdapterActionChanged()
                isInWatchLater = true
                watchLaterItem = it
            }
        }

    }

    private fun setupPlayer() {

        val playerAdapter = LeanbackPlayerAdapter(requireContext(), player, 100)

         playerGlue = VideoPlayerTvGlue(requireContext(), playerAdapter,watchLaterAction).apply {
            host = VideoSupportFragmentGlueHost(this@VideoPlayerTvFragment)
            playWhenPrepared()

            ChikiFetcher().fetchStreamingPlaylist(videoId).observe(this@VideoPlayerTvFragment) {
                val media: MediaItem = MediaItem.Builder().setUri(it[0].playlistUrl).build()
                player.addMediaItem(media)
                player.prepare()

                //seek video to save user watch time
                ChikiChikiDatabaseRepository.get().getWatchedVideo(videoId).observe(viewLifecycleOwner) {
                    if (it != null && it.watchedVideoTimeInMil != player?.contentDuration ) {

                        player?.seekTo(it.watchedVideoTimeInMil)

                    }
                }

            }

            setActionClickListener(this@VideoPlayerTvFragment)

        }

        playerGlue.addPlayerCallback(object : PlaybackGlue.PlayerCallback() {
            override fun onPreparedStateChanged(glue: PlaybackGlue?) {
                super.onPreparedStateChanged(glue)
                if (glue?.isPrepared == true) {
                }
            }

            override fun onPlayCompleted(glue: PlaybackGlue?) {
                super.onPlayCompleted(glue)
            }
        })

        //add video title
        playerGlue.title = videoTitle



        adapter = ArrayObjectAdapter(playerGlue.playbackRowPresenter).apply {
            add(playerGlue.controlsRow)
        }

        // Adds key listeners
        playerGlue.host.setOnKeyInterceptListener { view, keyCode, event ->

            if (!playerGlue.host.isControlsOverlayVisible &&
                keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN
            ) {
                Log.d("TESTLOG", "Intercepting BACK key for fragment navigation")
                activity?.onBackPressed()
                return@setOnKeyInterceptListener true
            }

            // Skips ahead when user presses DPAD_RIGHT
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.action == KeyEvent.ACTION_DOWN) {
                player.seekForward()
                return@setOnKeyInterceptListener true
            }

            // Rewinds when user presses DPAD_LEFT
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.action == KeyEvent.ACTION_DOWN) {
                player.seekBack()
                return@setOnKeyInterceptListener true
            }

            false
        }




    }


    override fun onActionClick(action: Action?) {


        if(!isInWatchLater) {
            ChikiChikiDatabaseRepository.get().addToWatchLater(
                WatchLater(
                    videoId,
                    videoTitle,
                    videoDescription,
                    videoPreviewPath,
                    videoDuration,
                    Date()
                )
            )
            action?.icon =
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_added_to_watchlater)
            playerGlue.notifyAdapterActionChanged()
        }
        else {

            ChikiChikiDatabaseRepository.get().removeFromWatchLater(watchLaterItem)
            action?.icon =
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_add_to_watchlater)
            playerGlue.notifyAdapterActionChanged()

        }


    }

    override fun onResume() {
        super.onResume()
        mediaSessionConnector.setPlayer(player)
        mediaSession.isActive = true
        player.prepare()
    }

    override fun onPause() {
        super.onPause()
        mediaSession.isActive = false
        mediaSessionConnector.setPlayer(null)
        player.stop()

    }

    override fun onDestroy() {
        super.onDestroy()

        //save how much the user watched if it is more than 30% or the video duration
        val current = player?.currentPosition
        val total = player?.contentDuration
        //default is 23:55
        val minDuration = total?.times((0.01))?.toLong() ?: (1434856).toLong()

        if (current != null) {
            if (current >= minDuration) {
                ChikiChikiDatabaseRepository.get().addWatchedVideo(WatchedVideo(videoId, current))
            }
        }

        mediaSession.release()
        player.release()
        Log.d("TESTLOG", "VIDEO PLAYER DESTROYED")
    }



    companion object {
        fun newInstance(videoID: String?, videoTitle: String?,videoDescription:String?,videoPreviewPath:String?,videoDuration:Int): VideoPlayerTvFragment {
            val args = Bundle()
            args.putString(ARG_VIDEO_ID, videoID)
            args.putString(ARG_VIDEO_TITLE, videoTitle)
            args.putString(ARG_VIDEO_DESC, videoDescription)
            args.putString(ARG_VIDEO_PREV, videoPreviewPath)
            args.putInt(ARG_VIDEO_DURATION, videoDuration)

            val fragment = VideoPlayerTvFragment()
            fragment.arguments = args
            return fragment
        }
    }


}