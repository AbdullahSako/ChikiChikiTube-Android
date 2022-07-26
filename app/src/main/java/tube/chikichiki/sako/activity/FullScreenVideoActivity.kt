package tube.chikichiki.sako.activity

import android.app.Activity
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.drawable.Icon
import android.os.*
import android.util.Log
import android.util.Rational
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.DefaultTimeBar

import tube.chikichiki.sako.R
import tube.chikichiki.sako.Utils
import tube.chikichiki.sako.api.ChikiFetcher
import tube.chikichiki.sako.view.CustomExoPlayerView
import java.util.*

private const val EXTRA_POSITION: String = "PLAYBACKPOSITION"
private const val EXTRA_PLAY_WHEN_READY: String = "PLAYWHENREADY"
private const val EXTRA_PLAYLIST_URL: String = "PLAYLISTURL"
private const val EXTRA_VIDEO_NAME: String = "VIDEONAME"
private const val EXTRA_VIDEO_ID:String="VIDEOID"
const val EXTRA_PLAYBACK_POSITION: String = "PLAYBACKPOSITION"
const val EXTRA_PLAY_WHEN_READY_BACK: String = "PLAYBACKWHENREADY"
private const val STATE_EXTRA_POSITION:String ="STATEPLAYBACKPOSITION"

class FullScreenVideoActivity : AppCompatActivity() {
    private var videoPlayer: ExoPlayer? = null
    private var playlistUrl: String? = null
    private lateinit var videoPlayerView:CustomExoPlayerView
    private var pIPPlayBackPosition:Long?=null
    private var videoId:UUID?=null
    private val handlerT:Handler=Handler(Looper.getMainLooper())
    private var runnableCancelled:Boolean=false
    private val runnable= object : Runnable{
        override fun run() {
            handlerT.removeCallbacksAndMessages(null)
            videoId?.let { ChikiFetcher().addAView(it,(videoPlayer?.currentPosition?.div(1000))?.toInt()) }
            if(!runnableCancelled) {
                handlerT.postDelayed(this, 10000)
            }
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_video)


        videoPlayerView = findViewById<CustomExoPlayerView>(R.id.video_player)
        val videoTitleTextView: TextView = videoPlayerView.findViewById(R.id.exo_player_view_video_title)
        val pipBtn:ImageButton = videoPlayerView.findViewById(R.id.pipBtn)

        //set up video player
        videoPlayer =
            ExoPlayer.Builder(this).setSeekBackIncrementMs(10000).setSeekForwardIncrementMs(10000)
                .build()

        //set controller hide time
        videoPlayerView.controllerShowTimeoutMs = 3000

        //bind exoplayer to view
        videoPlayerView.player = videoPlayer

        //add player listener
        exoPlayerListener(videoPlayerView)
        //setup player control view buttons
        exoPlayerCustomControllerViewSetUp()

        //get extras
        val playbackPosition = intent.extras?.getLong(EXTRA_POSITION)
        val playWhenReady = intent.extras?.getBoolean(EXTRA_PLAY_WHEN_READY)
        playlistUrl = intent.extras?.getString(EXTRA_PLAYLIST_URL)
        val videoName = intent.extras?.getString(EXTRA_VIDEO_NAME)
        videoId = intent.extras?.get(EXTRA_VIDEO_ID) as UUID


        //setup video title
        videoTitleTextView.visibility =
            View.VISIBLE //player control view video title is gone by default so it doesn't show up !fullscreen
        videoTitleTextView.text = videoName

        //setup media item
        val media: MediaItem = MediaItem.Builder().setUri(playlistUrl).build()
        videoPlayer?.addMediaItem(media)


        //set up playback position sent from video player fragment
        if (playbackPosition != null && savedInstanceState==null) {
            videoPlayer?.seekTo(playbackPosition)
        }
        if (playWhenReady != null) {
            videoPlayer?.playWhenReady = playWhenReady
        }


        //Picture in picture mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)){
                pipBtn.visibility = View.VISIBLE
                pipBtn.setOnClickListener {
                    enterPipMode()
                }
            }
        }


        //run runnable to add view
        handlerT.postDelayed(runnable,1000)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun enterPipMode() {

        val aspect=Rational(16,9)

        val params = PictureInPictureParams.Builder().setAspectRatio(aspect).build()

        enterPictureInPictureMode(params)
    }




    private fun exoPlayerListener(playerView: CustomExoPlayerView) {
        val exoPlayerProgressBar: ProgressBar =
            playerView.findViewById(R.id.exo_player_progress_bar)
        val controlPlayBtn: ImageButton = playerView.findViewById(R.id.control_view_play_btn)


        videoPlayer?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if (playbackState == Player.STATE_BUFFERING) {
                    exoPlayerProgressBar.visibility = View.VISIBLE
                } else if (playbackState == Player.STATE_READY) {
                    exoPlayerProgressBar.visibility = View.INVISIBLE
                }
                if(playbackState == Player.STATE_ENDED){
                    runnableCancelled=true
                }

            }

            //change play/pause button
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                if (isPlaying) {
                    //add flag to restrict device to sleep
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

                    //in miui phones adding or clearing the flag results in status bar showing so i am hiding it again
                    hideStatusBars()

                    //change player control view play button image to paused image
                    controlPlayBtn.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this@FullScreenVideoActivity, R.drawable.ic_pause_circle
                        )
                    )


                } else {
                    //add flag to allow device to sleep
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

                    //in miui phones adding or clearing the flag results in status bar showing so i am hiding it again
                    hideStatusBars()

                    //change player control view pause button image to play image
                    controlPlayBtn.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this@FullScreenVideoActivity,
                            R.drawable.ic_play_circle
                        )
                    )
                }
            }
        })


    }


    private fun exoPlayerCustomControllerViewSetUp() {
        val play: ImageButton = findViewById(R.id.control_view_play_btn)
        val forward: ImageButton = findViewById(R.id.control_view_forward_btn)
        val replay: ImageButton = findViewById(R.id.control_view_replay_btn)
        val fullscreen: ImageButton = findViewById(R.id.control_view_fullscreen_btn)
        val timeBar: DefaultTimeBar = findViewById(R.id.exo_progress)

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
        timeBar.setScrubberColor(ContextCompat.getColor(this, R.color.orange))
        timeBar.setPlayedColor(ContextCompat.getColor(this, R.color.icon_yellow))

        //send back playback position to video player fragment and close activity
        fullscreen.setOnClickListener {
            sendPlaybackDetailsBack()
            onBackPressed()
        }


    }


    private fun hideStatusBars() {

        //hide status bar
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }


    }

    private fun sendPlaybackDetailsBack() {
        videoPlayer?.pause()
        val data = intent.apply {
            putExtra(EXTRA_PLAYBACK_POSITION, videoPlayer?.currentPosition)
            putExtra(EXTRA_PLAY_WHEN_READY_BACK, videoPlayer?.playWhenReady)
        }
        setResult(Activity.RESULT_OK, data)
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration?
    ) {

        if(!isInPictureInPictureMode){
            videoPlayerView.useController=true

        }

        Utils.IsInPipMode = isInPictureInPictureMode

        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)

    }


    override fun onBackPressed() {
        sendPlaybackDetailsBack()
        super.onBackPressed()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        //prepares video player when initially opening full screen activity and when returning from other applications
        //if screen is locked this code isn't run
        if (hasFocus) {
            //prepares and plays video
            videoPlayer?.prepare()
        }
    }


    override fun onResume() {
        super.onResume()
        hideStatusBars()

    }

    override fun onStop() {
        super.onStop()
        Log.d("TESTLOG", "FULLSCREEN VIDEO PLAYER stopped")

        videoPlayer?.stop()

        Log.d("TESTLOG", "Runnable Stopped")
        runnableCancelled=true

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TESTLOG", "FULLSCREEN VIDEO PLAYER DESTROYED")
        videoPlayer?.release()


    }

    override fun onPause() {
        super.onPause()
        //if in pip mode continue playing the video
        if(Utils.IsInPipMode){
            videoPlayer?.prepare()
            videoPlayer?.play()
            videoPlayerView.useController=false
        }


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)


        //save position to saved instance state for when entering/leaving pip mode
        if(videoPlayer?.currentPosition != 0L) {

            videoPlayer?.currentPosition?.let { outState.putLong(STATE_EXTRA_POSITION, it) }
        }
        else{
            pIPPlayBackPosition?.let { outState.putLong(STATE_EXTRA_POSITION, it) }
        }


    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        //get position from saved instance state for when in pip mode / out of pip mode
        pIPPlayBackPosition = savedInstanceState.getLong(STATE_EXTRA_POSITION)

        //seek exoplayer with restored position
        pIPPlayBackPosition?.let { videoPlayer?.seekTo(it) }

    }


    companion object {
        fun newInstance(
            context: Context?,
            playbackPosition: Long?,
            playWhenReady: Boolean?,
            playlistUrl: String?,
            videoName: String?,
            videoId:UUID?
        ): Intent {
            return Intent(context, FullScreenVideoActivity::class.java).apply {
                putExtra(EXTRA_POSITION, playbackPosition)
                putExtra(EXTRA_PLAY_WHEN_READY, playWhenReady)
                putExtra(EXTRA_PLAYLIST_URL, playlistUrl)
                putExtra(EXTRA_VIDEO_NAME, videoName)
                putExtra(EXTRA_VIDEO_ID,videoId)
            }
        }
    }
}