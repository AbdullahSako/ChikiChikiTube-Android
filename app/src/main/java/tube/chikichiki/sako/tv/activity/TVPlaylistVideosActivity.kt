package tube.chikichiki.sako.tv.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import tube.chikichiki.sako.R
import tube.chikichiki.sako.tv.fragment.PlaylistVideosTvFragment

private const val EXTRA_PLAYLIST_ID="PLAYLISTID"
class TVPlaylistVideosActivity:FragmentActivity(R.layout.activity_tv_playlist_videos) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val playlistId = intent.extras?.getInt(EXTRA_PLAYLIST_ID)

        if (playlistId != null) {
            supportFragmentManager.beginTransaction().apply {
                replace(
                    R.id.tv_playlist_videos_fragment_container,
                    PlaylistVideosTvFragment.newInstance(playlistId)
                )
                commit()
            }
        }
    }

    companion object{
        fun newIntent(context:Context,playlistId: Int): Intent {
            val intent = Intent(context,TVPlaylistVideosActivity::class.java)
            intent.putExtra(EXTRA_PLAYLIST_ID,playlistId)
            return intent
        }
    }
}