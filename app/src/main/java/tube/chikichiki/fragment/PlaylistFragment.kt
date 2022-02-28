package tube.chikichiki.fragment

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf

import androidx.lifecycle.Observer

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import tube.chikichiki.R

import tube.chikichiki.adapter.PlaylistAdapter
import tube.chikichiki.api.ChikiFetcher
import tube.chikichiki.model.VideoPlaylist

private const val ARG_CHANNEL_ID="CHANNEL_ID"
class PlaylistFragment : Fragment(R.layout.fragment_playlist) {

    private lateinit var grainAnimation: AnimationDrawable
    private lateinit var playListRecyclerView:RecyclerView
    private var channelId:Int ?= null



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        channelId=arguments?.getInt(ARG_CHANNEL_ID)

        val noPlaylistsTextView:TextView=view.findViewById(R.id.no_playlists_textView)
        val progressBar:ProgressBar=view.findViewById(R.id.progressBar)
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
        ChikiFetcher().fetchPlaylists().observe(viewLifecycleOwner, Observer {
            val listOfPlaylists=playlistsOfChannel(it)
            playListRecyclerView.adapter=PlaylistAdapter(listOfPlaylists)

            //remove progressbar after loading playlists
            progressBar.visibility=View.GONE

            //if there are no playlists for the channel add textview telling the user
            if(listOfPlaylists.isEmpty()){
                noPlaylistsTextView.visibility=View.VISIBLE
            }
        })
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

}