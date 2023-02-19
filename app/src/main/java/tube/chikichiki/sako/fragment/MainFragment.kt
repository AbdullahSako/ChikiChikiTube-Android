package tube.chikichiki.sako.fragment

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tube.chikichiki.sako.R
import tube.chikichiki.sako.Utils
import tube.chikichiki.sako.adapter.ChannelAdapter
import tube.chikichiki.sako.viewModel.ChannelViewModel


class MainFragment : Fragment(R.layout.fragment_main), ChannelAdapter.ChannelViewClick {

    private lateinit var grainAnimation: AnimationDrawable
    private var channelViewModel: ChannelViewModel? = null
    private lateinit var channelRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        channelViewModel = activity?.let { ViewModelProvider(it).get(ChannelViewModel::class.java) }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressbar: ProgressBar = view.findViewById(R.id.progressBar)
        val constraint: ConstraintLayout = view.findViewById(R.id.main_fragment_constraint_layout)
        channelRecyclerView = view.findViewById(R.id.channel_recycler_view)

        //set recycler view layout manager
        channelRecyclerView.layoutManager = LinearLayoutManager(context)

        //set fragment background animation and start it
        constraint.apply {
            setBackgroundResource(R.drawable.grain_animation)
            grainAnimation = background as AnimationDrawable
        }
        grainAnimation.start()

        //retrieve channel list from api
        channelViewModel?.channelItemLiveData?.observe(viewLifecycleOwner) {

            val filteredChannels = Utils.sortChannels(it)

            val adapter = ChannelAdapter(filteredChannels)
            adapter.setChannelViewClickListener(this@MainFragment)
            channelRecyclerView.adapter = adapter


            //remove progressbar after loading channel list
            progressbar.visibility = View.GONE

        }


    }


    override fun onItemClick(view: View, channelId: Int, channelHandle: String) {


        //hide tool bar and bottom nav bar and disable transition to start when opening a channel
        val r = Runnable {
            activity?.findViewById<MotionLayout>(R.id.activity_main_motion_layout)?.progress = 0f
            activity?.findViewById<MotionLayout>(R.id.activity_main_motion_layout)
                ?.setTransition(R.id.main_end, R.id.main_end)
        }
        activity?.findViewById<MotionLayout>(R.id.activity_main_motion_layout)?.transitionToEnd(r)



        parentFragmentManager.beginTransaction().apply {
            setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
            add(R.id.main_fragment_container, ChannelFragment.newInstance(channelId, channelHandle))
            commit()
        }

    }


}