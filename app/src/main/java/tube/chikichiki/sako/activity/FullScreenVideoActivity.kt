package tube.chikichiki.sako.activity

import android.app.Activity
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Paint.Cap
import android.graphics.drawable.Icon
import android.net.Uri
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.material.snackbar.Snackbar

import tube.chikichiki.sako.R
import tube.chikichiki.sako.Utils
import tube.chikichiki.sako.api.ChikiFetcher
import tube.chikichiki.sako.database.ChikiChikiDatabaseRepository
import tube.chikichiki.sako.model.Caption
import tube.chikichiki.sako.model.WatchLater
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
private const val EXTRA_VIDEO_DESCRIPTION:String="VIDEODESCRIPTION"
private const val EXTRA_VIDEO_DURATION:String="VIDEODURATION"
private const val EXTRA_VIDEO_THUMBNAIL:String="VIDEOTHUMBNAIL"
private const val EXTRA_VIDEO_CAPTION:String = "VIDEO_CAPTION"

class FullScreenVideoActivity : AppCompatActivity() {
    private var videoPlayer: ExoPlayer? = null
    private var playlistUrl: String? = null
    private lateinit var videoPlayerView:CustomExoPlayerView
    private var pIPPlayBackPosition:Long?=null
    private var videoId:UUID?=null
    private val handlerT:Handler=Handler(Looper.getMainLooper())
    private var runnableCancelled:Boolean=false
    private var runnableAddViewPaused:Boolean =false
    private val runnable= object : Runnable{
        override fun run() {
            handlerT.removeCallbacksAndMessages(null)

            if(!runnableAddViewPaused){
                videoId?.let { ChikiFetcher().addAView(it,(videoPlayer?.currentPosition?.div(1000))?.toInt()) }
            }

            if(!runnableCancelled) {
                handlerT.postDelayed(this, 5000)
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
        val videoDescription = intent.extras?.get(EXTRA_VIDEO_DESCRIPTION) as String
        val videoDuration = intent.extras?.get(EXTRA_VIDEO_DURATION) as Int
        val videoThumbnailPath = intent.extras?.get(EXTRA_VIDEO_THUMBNAIL) as String

        var videoCaption:Caption? = null
        if(intent.getSerializableExtra(EXTRA_VIDEO_CAPTION) != null){
            videoCaption = intent.getSerializableExtra(EXTRA_VIDEO_CAPTION) as Caption
        }

        //setup video title
        videoTitleTextView.visibility =
            View.VISIBLE //player control view video title is gone by default so it doesn't show up !fullscreen
        videoTitleTextView.text = videoName


        //init caption
        val subtitles = arrayListOf<MediaItem.SubtitleConfiguration>()

        if(videoCaption!= null){
            val uri = Uri.parse("https://vtr.chikichiki.tube" + videoCaption.captionPath)
            subtitles.add(
                MediaItem.SubtitleConfiguration.Builder(uri)
                    .setMimeType(MimeTypes.TEXT_VTT)
                    .setLanguage("en")
                    .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                    .build())
        }


        //setup media item
        val media: MediaItem = MediaItem.Builder().setUri(playlistUrl).setSubtitleConfigurations(subtitles).build()
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


        //to add a view it is required by peertube to send a call every 10 seconds
        //run runnable to add view
        handlerT.postDelayed(runnable,1000)


        //setup watch later
        setUpWatchLater(videoPlayerView, videoId,videoName,videoDescription,videoThumbnailPath,videoDuration)

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

                    //runnable add view resumed
                    runnableAddViewPaused = false

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

                    //runnable add view paused
                    runnableAddViewPaused = true

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

    private fun setUpWatchLater(videoPlayerView:View,videoId:UUID?,videoName:String?,videoDescription: String,videoThumbnailPath:String,videoDuration: Int){
        val watchLaterBtn:ImageButton = videoPlayerView.findViewById(R.id.control_view_watchlater)

        if (videoId != null) {
            ChikiChikiDatabaseRepository.get().getWatchLaterItem(videoId).observe(this){ watchLaterItem ->

                if(watchLaterItem == null){ //if not added yet
                    watchLaterBtn.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_add_to_watchlater))

                    watchLaterBtn.setOnClickListener {
                        ChikiChikiDatabaseRepository.get().addToWatchLater(WatchLater(videoId,
                            videoName.toString(),videoDescription,videoThumbnailPath,videoDuration,Date()))
                    }
                }
                else{ //if already added to watch later
                    watchLaterBtn.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_added_to_watchlater))

                    watchLaterBtn.setOnClickListener {
                        ChikiChikiDatabaseRepository.get().removeFromWatchLater(watchLaterItem)
                    }

                }

            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
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
            videoId:UUID?,
            videoDescription:String,
            videoDuration:Int,
            videoThumbnailPath:String,
            videoCaption:Caption?
        ): Intent {
            return Intent(context, FullScreenVideoActivity::class.java).apply {
                putExtra(EXTRA_POSITION, playbackPosition)
                putExtra(EXTRA_PLAY_WHEN_READY, playWhenReady)
                putExtra(EXTRA_PLAYLIST_URL, playlistUrl)
                putExtra(EXTRA_VIDEO_NAME, videoName)
                putExtra(EXTRA_VIDEO_ID,videoId)
                putExtra(EXTRA_VIDEO_DESCRIPTION,videoDescription)
                putExtra(EXTRA_VIDEO_THUMBNAIL,videoThumbnailPath)
                putExtra(EXTRA_VIDEO_DURATION,videoDuration)
                putExtra(EXTRA_VIDEO_CAPTION,videoCaption)
            }
        }
    }
}