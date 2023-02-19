package tube.chikichiki.sako.fragment

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import tube.chikichiki.sako.R
import tube.chikichiki.sako.Utils.playlistsOfChannel
import tube.chikichiki.sako.adapter.PlaylistAdapter
import tube.chikichiki.sako.api.ChikiFetcher
import tube.chikichiki.sako.model.VideoPlaylist

private const val ARG_CHANNEL_ID = "CHANNEL_ID"

class PlaylistFragment : Fragment(R.layout.fragment_playlist), PlaylistAdapter.PlaylistViewClick {

    private lateinit var grainAnimation: AnimationDrawable
    private lateinit var playListRecyclerView: RecyclerView
    private var channelId: Int? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        channelId = arguments?.getInt(ARG_CHANNEL_ID)


        val rootLayout: ConstraintLayout = view.findViewById(R.id.root_layout_playlist)
        playListRecyclerView = view.findViewById(R.id.playlist_recycler_view)

        //set recycler view layout manager
        playListRecyclerView.layoutManager = LinearLayoutManager(context)


        //set fragment background animation and start it
        rootLayout.apply {
            setBackgroundResource(R.drawable.grain_animation)
            grainAnimation = background as AnimationDrawable
        }
        grainAnimation.start()

        //get playlists from api
        ChikiFetcher().fetchPlaylists().observe(viewLifecycleOwner) { playlistList ->

            if (playlistList.size == 100) {
                //fetch next 100 playlists
                ChikiFetcher().fetchPlaylists(100).observe(viewLifecycleOwner) {
                    loadPlaylists(playlistList + it)
                }
            } else {
                loadPlaylists(playlistList)
            }

        }

        tabLayoutOnReselectGoToPositionZero()
    }

    private fun loadPlaylists(list: List<VideoPlaylist>) {
        val noPlaylistsTextView: TextView? = view?.findViewById(R.id.no_playlists_textView)
        val progressBar: ProgressBar? = view?.findViewById(R.id.progressBar)

        val listOfPlaylists = playlistsOfChannel(list, channelId)
        val playlistAdapter = PlaylistAdapter(listOfPlaylists)

        playListRecyclerView.apply {
            playlistAdapter.setPlaylistViewClickListener(this@PlaylistFragment)
            adapter = playlistAdapter
        }

        //remove progressbar after loading playlists
        progressBar?.visibility = View.GONE

        //if there are no playlists for the channel add textview telling the user
        if (listOfPlaylists.isEmpty()) {
            noPlaylistsTextView?.visibility = View.VISIBLE
        }
    }


    private fun tabLayoutOnReselectGoToPositionZero() {

        val tabLayout = activity?.findViewById<TabLayout>(R.id.channel_tab_layout)
        tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                if (tab?.text == "Playlists") {
                    playListRecyclerView.smoothScrollToPosition(0)
                }
            }

        })
    }

    companion object {
        //returns a bundle based on arg_channel_id string key
        fun getNavArgsBundle(channelId: Int): Bundle {
            return bundleOf(ARG_CHANNEL_ID to channelId)
        }
    }


    override fun onPlayListClick(view: View, playlistId: Int) {
        //just in case parent motion layout state isnt at start
        //activity?.findViewById<MotionLayout>(R.id.channel_fragment_motion_layout)?.transitionToStart()

        parentFragmentManager.beginTransaction().apply {
            setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
            add(R.id.root_layout_playlist, PlaylistVideosFragment.newInstance(playlistId))
            commit()
        }
    }

}