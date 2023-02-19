package tube.chikichiki.sako.tv.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import tube.chikichiki.sako.R
import tube.chikichiki.sako.tv.fragment.PlaylistVideosTvFragment

private const val EXTRA_PLAYLIST_ID = "PLAYLISTID"
private const val EXTRA_PLAYLIST_NAME = "PLAYLISTNAME"

class TVPlaylistVideosActivity : FragmentActivity(R.layout.activity_tv_playlist_videos) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val playlistId = intent.extras?.getInt(EXTRA_PLAYLIST_ID)
        val playlistName = intent.extras?.getString(EXTRA_PLAYLIST_NAME)

        if (playlistId != null && playlistName != null) {
            supportFragmentManager.beginTransaction().apply {
                replace(
                    R.id.tv_playlist_videos_fragment_container,
                    PlaylistVideosTvFragment.newInstance(playlistId, playlistName)
                )
                commit()
            }
        }
    }

    companion object {
        fun newIntent(context: Context, playlistId: Int, playlistName: String): Intent {
            val intent = Intent(context, TVPlaylistVideosActivity::class.java)
            intent.putExtra(EXTRA_PLAYLIST_ID, playlistId)
            intent.putExtra(EXTRA_PLAYLIST_NAME, playlistName)
            return intent
        }
    }
}