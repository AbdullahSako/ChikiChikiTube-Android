package tube.chikichiki.fragment

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tube.chikichiki.viewModel.ChannelViewModel
import tube.chikichiki.R
import tube.chikichiki.activity.ChannelActivity
import tube.chikichiki.adapter.ChannelAdapter
import tube.chikichiki.model.VideoChannel


class MainFragment : Fragment(R.layout.fragment_main) ,ChannelAdapter.ChannelViewClick {

    private lateinit var grainAnimation: AnimationDrawable
    private var channelViewModel: ChannelViewModel? = null
    private lateinit var channelRecyclerView:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        channelViewModel= activity?.let { ViewModelProvider(it).get(ChannelViewModel::class.java) }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressbar:ProgressBar=view.findViewById(R.id.progressBar)
        val constraint:ConstraintLayout=view.findViewById(R.id.main_fragment_constraint_layout)
        channelRecyclerView=view.findViewById(R.id.channel_recycler_view)

        //set recycler view layout manager
        channelRecyclerView.layoutManager=LinearLayoutManager(context)

        //set fragment background animation and start it
        constraint.apply {
            setBackgroundResource(R.drawable.grain_animation)
            grainAnimation= background as AnimationDrawable
        }
        grainAnimation.start()

        //retrieve channel list from api
        channelViewModel?.channelItemLiveData?.observe(viewLifecycleOwner) {

            val sortedChannels= arrayOf("gakinotsukai","gottsueekanji","knightscoop","suiyoubinodowntown","documental","lincoln","downtownnow","worlddowntown","heyheyhey","matsumotoke","ashitagaarusa","mhk","suberanaihanashi","visualbum","hitoshimatsumotostore")
            val temp:MutableList<Pair<Int,VideoChannel>> = mutableListOf()
            val leftOver= mutableListOf<VideoChannel>()

            //sort channels based on sorted channels array
            it.forEach {
                val index=sortedChannels.indexOf(it.channelHandle)
                if(index!=-1){
                    temp.add(index to it)
                }
                else{
                    leftOver.add(it)
                }
            }

            temp.sortBy {it.first}
            leftOver.forEach {leftOverListItem -> temp.add(it.size to leftOverListItem) }

            val sorted:MutableList<VideoChannel> = mutableListOf()

            temp.forEach { sorted.add(it.second) }


            //remove empty channels
            val adapter = ChannelAdapter(sorted.filter { it.channelHandle != "root_channel" && it.channelHandle!="fearfulkyochan" && it.channelHandle!="chikichikitube" })
            adapter.setChannelViewClickListener(this@MainFragment)
            channelRecyclerView.adapter = adapter

            //remove progressbar after loading channel list
            progressbar.visibility = View.GONE

        }


    }


    override fun onItemClick(view:View, channelId: Int, channelHandle:String) {
        val intent= ChannelActivity.newInstance(activity,channelId,channelHandle)
        startActivity(intent)
    }


}