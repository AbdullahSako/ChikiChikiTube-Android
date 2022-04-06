package tube.chikichiki.sako.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.mediarouter.app.MediaRouteButton
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaLoadRequestData
import com.google.android.gms.cast.framework.*
import tube.chikichiki.sako.R
import tube.chikichiki.sako.view.CustomExoPlayerView

private const val EXTRA_POSITION:String="PLAYBACKPOSITION"
private const val EXTRA_PLAY_WHEN_READY:String="PLAYWHENREADY"
private const val EXTRA_PLAYLIST_URL:String="PLAYLISTURL"
private const val EXTRA_VIDEO_NAME:String="VIDEONAME"
const val EXTRA_PLAYBACK_POSITION:String="PLAYBACKPOSITION"
const val EXTRA_PLAY_WHEN_READY_BACK:String="PLAYBACKWHENREADY"
class FullScreenVideoActivity : AppCompatActivity() {
    private var videoPlayer:ExoPlayer ?=null
    private var playlistUrl:String?=null
    private var mCastSession: CastSession? = null
    private lateinit var mSessionManager: SessionManager
    private val mSessionManagerListener: SessionManagerListener<CastSession> =
        SessionManagerListenerImpl()
    // only for testing cast for now
    private inner class SessionManagerListenerImpl : SessionManagerListener<CastSession> {
        override fun onSessionEnded(p0: CastSession, p1: Int) {
            finish()
        }

        override fun onSessionEnding(p0: CastSession) {

        }

        override fun onSessionResumeFailed(p0: CastSession, p1: Int) {
        }

        override fun onSessionResumed(p0: CastSession, p1: Boolean) {
        }

        override fun onSessionResuming(p0: CastSession, p1: String) {
        }

        override fun onSessionStartFailed(p0: CastSession, p1: Int) {
            Log.d("TESTLOG",p0.requestStatus().toString()+ " "+p1)
        }

        override fun onSessionStarted(p0: CastSession, p1: String) {
            val mediaInfo = //TODO LOGGER
                MediaInfo.Builder("https://vtr.chikichiki.tube/download/streaming-playlists/hls/videos/a7adc5f3-2485-473f-af37-5075bf60932a-720-fragmented.mp4")
                    .setStreamType(MediaInfo.STREAM_TYPE_LIVE)
                    .setStreamDuration((33 * 1000).toLong())
                    .setContentType("video/mp4")
                    .build()

            val remoteMediaClient = p0.remoteMediaClient
            remoteMediaClient?.load(MediaLoadRequestData.Builder().setMediaInfo(mediaInfo).build())
            Log.d("TESTLOG",p0.sessionId.toString())
            p0.remoteMediaClient?.play()


        }

        override fun onSessionStarting(p0: CastSession) {


        }

        override fun onSessionSuspended(p0: CastSession, p1: Int) {
            Log.d("TESTLOG",p0.requestStatus().toString()+ " "+p1)
        }

    }




        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_video)

        val videoPlayerView=findViewById<CustomExoPlayerView>(R.id.video_player)
        val videoTitleTextView:TextView=videoPlayerView.findViewById(R.id.exo_player_view_video_title)
        mSessionManager = CastContext.getSharedInstance(this).sessionManager

        val mediaRouteButton = findViewById<View>(R.id.media_route_button) as MediaRouteButton
        CastButtonFactory.setUpMediaRouteButton(applicationContext,mediaRouteButton)

        //set up video player
        videoPlayer=ExoPlayer.Builder(this).setSeekBackIncrementMs(10000).setSeekForwardIncrementMs(10000).build()

        //set controller hide time
        videoPlayerView.controllerShowTimeoutMs=3000

        //bind exoplayer to view
        videoPlayerView.player = videoPlayer

        //add player listener
        exoPlayerListener(videoPlayerView)
        //setup player control view buttons
        exoPlayerCustomControllerViewSetUp()

        //get extras
        val playbackPosition=intent.extras?.getLong(EXTRA_POSITION)
        val playWhenReady=intent.extras?.getBoolean(EXTRA_PLAY_WHEN_READY)
        playlistUrl=intent.extras?.getString(EXTRA_PLAYLIST_URL)
        val videoName = intent.extras?.getString(EXTRA_VIDEO_NAME)




        //setup video title
        videoTitleTextView.visibility=View.VISIBLE //player control view video title is gone by default so it doesn't show up !fullscreen
        videoTitleTextView.text=videoName

        //setup media item
        val media: MediaItem = MediaItem.Builder().setUri(playlistUrl).build()
        videoPlayer?.addMediaItem(media)

        //set up playback position sent from video player fragment
        if (playbackPosition != null) {
            videoPlayer?.seekTo(playbackPosition)
        }
        if (playWhenReady != null) {
            videoPlayer?.playWhenReady=playWhenReady
        }





    }

    private fun exoPlayerListener(playerView: CustomExoPlayerView){
        val exoPlayerProgressBar: ProgressBar =playerView.findViewById(R.id.exo_player_progress_bar)

        videoPlayer?.addListener(object : Player.Listener{
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if(playbackState== Player.STATE_BUFFERING){
                    exoPlayerProgressBar.visibility=View.VISIBLE
                }
                else if(playbackState == Player.STATE_READY){
                    exoPlayerProgressBar.visibility=View.INVISIBLE
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
                play.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this,
                        R.drawable.ic_play_circle
                    )
                )
            } else {
                videoPlayer?.prepare()
                videoPlayer?.play()
                play.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this,
                        R.drawable.ic_pause_circle
                    )
                )
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

    private fun hideStatusBars(){

        //hide status bar
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

    }

    private fun sendPlaybackDetailsBack(){
        videoPlayer?.pause()
        val data=intent.apply {
            putExtra(EXTRA_PLAYBACK_POSITION,videoPlayer?.currentPosition)
            putExtra(EXTRA_PLAY_WHEN_READY_BACK,videoPlayer?.playWhenReady)
        }
        setResult(Activity.RESULT_OK,data)
    }

    override fun onBackPressed() {
        sendPlaybackDetailsBack()
        super.onBackPressed()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        //prepares video player when initially opening full screen activity and when returning from other applications
        //if screen is locked this code isn't run
        if(hasFocus){
            //prepares and plays video
            videoPlayer?.prepare()
        }
    }

    override fun onResume() {
        super.onResume()
        hideStatusBars()
        mCastSession = mSessionManager.currentCastSession
        mSessionManager.addSessionManagerListener(mSessionManagerListener, CastSession::class.java)

    }

    override fun onPause() {
        super.onPause()
        mSessionManager.removeSessionManagerListener(mSessionManagerListener, CastSession::class.java)
        mCastSession = null
    }

    override fun onStop() {
        super.onStop()
        Log.d("TESTLOG", "FULLSCREEN VIDEO PLAYER stopped")

        videoPlayer?.stop()

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TESTLOG", "FULLSCREEN VIDEO PLAYER DESTROYED")
        videoPlayer?.release()
    }



    companion object{
        fun newInstance(context: Context?,playbackPosition:Long?,playWhenReady:Boolean?,playlistUrl:String?,videoName:String?):Intent {
            return Intent(context,FullScreenVideoActivity::class.java).apply {
                putExtra(EXTRA_POSITION,playbackPosition)
                putExtra(EXTRA_PLAY_WHEN_READY,playWhenReady)
                putExtra(EXTRA_PLAYLIST_URL,playlistUrl)
                putExtra(EXTRA_VIDEO_NAME,videoName)
            }
        }
    }
}