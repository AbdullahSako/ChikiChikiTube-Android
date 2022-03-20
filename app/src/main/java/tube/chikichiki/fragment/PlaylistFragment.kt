package tube.chikichiki.fragment

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tube.chikichiki.R
import tube.chikichiki.adapter.PlaylistAdapter
import tube.chikichiki.api.ChikiFetcher
import tube.chikichiki.model.VideoPlaylist

private const val ARG_CHANNEL_ID="CHANNEL_ID"
private const val TAG="playlistVideos"
class PlaylistFragment : Fragment(R.layout.fragment_playlist),PlaylistAdapter.PlaylistViewClick {

    private lateinit var grainAnimation: AnimationDrawable
    private lateinit var playListRecyclerView:RecyclerView
    private var channelId:Int ?= null



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        channelId=arguments?.getInt(ARG_CHANNEL_ID)


        val rootLayout: ConstraintLayout =view.findViewById(R.id.root_layout_playlist)
        playListRecyclerView=view.findViewById(R.id.playlist_recycler_view)

        //set recycler view layout manager
        playListRecyclerView.layoutManager=LinearLayoutManager(context)



        //set fragment background animation and start it
        rootLayout.apply {
            setBackgroundResource(R.drawable.grain_animation)
            grainAnimation= background as AnimationDrawable
        }
        grainAnimation.start()

        //get playlists from api
        ChikiFetcher().fetchPlaylists().observe(viewLifecycleOwner) { playlistList ->


            if(playlistList.size==100){
                //fetch next 100 playlists
                ChikiFetcher().fetchPlaylists(100).observe(viewLifecycleOwner) {
                    loadPlaylists(playlistList + it)
                }
            }
            else{
                loadPlaylists(playlistList)
            }

        }
    }

    private fun loadPlaylists(list: List<VideoPlaylist>){
        val noPlaylistsTextView:TextView?= view?.findViewById(R.id.no_playlists_textView)
        val progressBar:ProgressBar?=view?.findViewById(R.id.progressBar)

        val listOfPlaylists = playlistsOfChannel(list)
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

    //get playlists of a channel based on channel id
    private fun playlistsOfChannel(list:List<VideoPlaylist>):List<VideoPlaylist>{
        val filteredList: MutableList<VideoPlaylist> = mutableListOf()

        list.forEach {
            if(it.videoChannel.id==channelId){
                filteredList.add(it)
            }
        }

        return filteredList
    }

    companion object{
        //returns a bundle based on arg_channel_id string key
        fun getNavArgsBundle(channelId:Int):Bundle{
            return bundleOf(ARG_CHANNEL_ID to channelId)
        }


    }

    override fun onPlayListClick(view: View, playlistId: Int) {
        //just in case parent motion layout state isnt at start
        activity?.findViewById<MotionLayout>(R.id.channel_activity_motion_layout)?.transitionToStart()



        parentFragmentManager.beginTransaction().apply {
            replace(R.id.root_layout_playlist,PlaylistVideosFragment.newInstance(playlistId))
            addToBackStack(TAG)
            commit()
        }
    }

}