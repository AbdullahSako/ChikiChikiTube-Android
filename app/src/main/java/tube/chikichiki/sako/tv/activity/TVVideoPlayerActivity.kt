package tube.chikichiki.sako.tv.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import tube.chikichiki.sako.R
import tube.chikichiki.sako.tv.fragment.VideoPlayerTvFragment

const val TAG = "VideoPlayerTag"
private const val EXTRA_VIDEO_ID = "VIDEOID"
private const val EXTRA_VIDEO_TITLE = "VIDEOTITLE"
private const val EXTRA_VIDEO_DESC = "VIDEODESC"
private const val EXTRA_VIDEO_DURATION = "VIDEODURATION"
private const val EXTRA_VIDEO_PREV = "VIDEOPREV"

class TVVideoPlayerActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_video_player)

        val videoId = intent.extras?.getString(EXTRA_VIDEO_ID)
        val title = intent.extras?.getString(EXTRA_VIDEO_TITLE)
        val desc = intent.extras?.getString(EXTRA_VIDEO_DESC)
        val previewPath = intent.extras?.getString(EXTRA_VIDEO_PREV)
        val duration = intent.extras?.getInt(EXTRA_VIDEO_DURATION) ?: 0


        supportFragmentManager.beginTransaction().apply {
            add(
                R.id.tvVideoFragment,
                VideoPlayerTvFragment.newInstance(videoId, title, desc, previewPath, duration),
                TAG
            )
            commit()
        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // This part is necessary to ensure that getIntent returns the latest intent when
        // Activity is started. By default, getIntent() returns the initial intent
        // that was set from another activity that started VideoExampleActivity. However, we need
        // to update this intent when for example, user clicks on another video when the currently
        // playing video is in PIP mode, and a new video needs to be started.
        setIntent(intent)
    }

    companion object {
        fun newInstance(
            context: Context?,
            videoId: String,
            videoTitle: String,
            videoDescription: String?,
            videoPreviewPath: String,
            videoDuration: Int
        ): Intent {
            return Intent(context, TVVideoPlayerActivity::class.java).apply {
                putExtra(EXTRA_VIDEO_ID, videoId)
                putExtra(EXTRA_VIDEO_TITLE, videoTitle)
                putExtra(EXTRA_VIDEO_DESC, videoDescription)
                putExtra(EXTRA_VIDEO_PREV, videoPreviewPath)
                putExtra(EXTRA_VIDEO_DURATION, videoDuration)
            }
        }
    }
}