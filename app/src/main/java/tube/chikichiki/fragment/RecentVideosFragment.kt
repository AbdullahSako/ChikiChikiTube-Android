package tube.chikichiki.fragment

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tube.chikichiki.R
import tube.chikichiki.adapter.VideoAdapter
import tube.chikichiki.viewModel.RecentVideosViewModel


class RecentVideosFragment : Fragment(R.layout.fragment_recent_videos) {
    private lateinit var grainAnimation: AnimationDrawable
    private lateinit var recentVideosViewModel: RecentVideosViewModel
    private lateinit var recentRecyclerView: RecyclerView
    private lateinit var videoAdapter:VideoAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recentVideosViewModel= ViewModelProvider(this).get(RecentVideosViewModel::class.java)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressbar: ProgressBar =view.findViewById(R.id.progressBar)
        val constraint: ConstraintLayout =view.findViewById(R.id.recent_constraint_layout)
        recentRecyclerView =view.findViewById(R.id.recent_videos_recycler_view)

        //set recycler view layout manager
        recentRecyclerView.layoutManager=LinearLayoutManager(context)

        //set fragment background animation and start it
        constraint.apply {
            setBackgroundResource(R.drawable.grain_animation)
            grainAnimation= background as AnimationDrawable
        }
        grainAnimation.start()


        //retrieve video list from api
        recentVideosViewModel.recentVideosLiveData.observe(viewLifecycleOwner, Observer {
            videoAdapter= VideoAdapter()
            videoAdapter.submitList(it)
            recentRecyclerView.adapter=videoAdapter


            //remove progressbar after loading video list
            progressbar.visibility=View.GONE
        })

    }

}