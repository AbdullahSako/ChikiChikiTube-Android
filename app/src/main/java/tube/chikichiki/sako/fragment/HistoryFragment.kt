package tube.chikichiki.sako.fragment

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tube.chikichiki.sako.R
import tube.chikichiki.sako.adapter.HistoryVerticalAdapter
import tube.chikichiki.sako.database.ChikiChikiDatabaseRepository
import tube.chikichiki.sako.model.HistoryVideoInfo
import java.util.*

class HistoryFragment:Fragment(R.layout.fragment_history),HistoryVerticalAdapter.HistoryViewClick,HistoryVerticalAdapter.HistoryRemoveClick {
    private lateinit var grainAnimation: AnimationDrawable


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rootView:ConstraintLayout = view.findViewById(R.id.history_root_view)
        val recyclerView:RecyclerView=view.findViewById(R.id.history_recycler_view)

        //set fragment background animation and start it
        rootView.apply {
            setBackgroundResource(R.drawable.grain_animation)
            grainAnimation= background as AnimationDrawable
        }
        grainAnimation.start()

        //setup up recycler view
        setupRecyclerView(recyclerView)


    }

    private fun setupRecyclerView(recyclerView: RecyclerView){

        recyclerView.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)

        ChikiChikiDatabaseRepository.get().getHistoryBig().observe(viewLifecycleOwner){
            val adp = HistoryVerticalAdapter(it)

            //set video on click listener
            adp.setHistoryViewClickListener(this)

            //set remove on click listener
            adp.setHistoryRemoveClickListener(this)

            recyclerView.adapter = adp
        }



    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        //remove fragment on back press instead of closing activity
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                //remove support fragment
                parentFragmentManager.beginTransaction().remove(this@HistoryFragment).commit()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }

    override fun onItemClick(
        videoId: UUID,
        videoName: String,
        videoDescription: String,
        previewPath: String,
        duration: Int
    ) {
        //hides main activity toolbar and bottom nav bar by progressing motion layout
        activity?.findViewById<MotionLayout>(R.id.activity_main_motion_layout)?.transitionToEnd()

        //opens a video by clicking on video view in history recycler view
        requireActivity().supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(R.anim.slide_up,0)
            replace(R.id.video_container,VideoPlayerFragment.newInstance(videoId,videoName,videoDescription, previewPath,duration))
            commit()
        }
    }

    override fun onRemoveClick(historyItem: HistoryVideoInfo) {
        ChikiChikiDatabaseRepository.get().removeFromHistory(historyItem)
    }


}