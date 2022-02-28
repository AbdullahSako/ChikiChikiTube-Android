package tube.chikichiki.fragment

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tube.chikichiki.viewModel.ChannelViewModel
import tube.chikichiki.R
import tube.chikichiki.adapter.ChannelAdapter


class MainFragment : Fragment(R.layout.fragment_main) ,ChannelAdapter.ChannelViewClick {

    private lateinit var grainAnimation: AnimationDrawable
    private lateinit var channelViewModel: ChannelViewModel
    private lateinit var channelRecyclerView:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        channelViewModel= ViewModelProvider(this).get(ChannelViewModel::class.java)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressbar:ProgressBar=view.findViewById(R.id.progressBar)
        val constraint:ConstraintLayout=view.findViewById(R.id.main_fragment_constraint_layout)
        channelRecyclerView=view.findViewById<RecyclerView>(R.id.channel_recycler_view)

        //set recycler view layout manager
        channelRecyclerView.layoutManager=LinearLayoutManager(context)

        //set fragment background animation and start it
        constraint.apply {
            setBackgroundResource(R.drawable.grain_animation)
            grainAnimation= background as AnimationDrawable
        }
        grainAnimation.start()

        //retrieve channel list from api
        channelViewModel.channelItemLiveData.observe(viewLifecycleOwner, Observer {

            val adapter=ChannelAdapter(it)
            adapter.setChannelViewClickListener(this@MainFragment)
            channelRecyclerView.adapter=adapter

            //remove progressbar after loading channel list
            progressbar.visibility=View.GONE

        })


    }


    override fun onItemClick(view:View, channelId: Int, channelHandle:String) {
        val action=MainFragmentDirections.actionMainFragmentToChannelActivity2(channelId,channelHandle)
        view.findNavController().navigate(action)
    }


}