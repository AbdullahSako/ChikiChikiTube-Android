package tube.chikichiki.sako.fragment

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tube.chikichiki.sako.R
import tube.chikichiki.sako.adapter.HistoryHorizontalAdapter
import tube.chikichiki.sako.database.ChikiChikiDatabaseRepository
import java.util.*

class LibraryFragment : Fragment(R.layout.fragment_library),
    HistoryHorizontalAdapter.HistoryViewClick {
    private lateinit var grainAnimation: AnimationDrawable
    private lateinit var recyclerAdapter: HistoryHorizontalAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rootView: ConstraintLayout = view.findViewById(R.id.library_root_view)
        val historyRecyclerView: RecyclerView =
            view.findViewById(R.id.library_history_recycler_view)

        //set fragment background animation and start it
        rootView.apply {
            setBackgroundResource(R.drawable.grain_animation)
            grainAnimation = background as AnimationDrawable
        }
        grainAnimation.start()


        //setup recyclerview
        setupRecyclerView(historyRecyclerView)

        //setup buttons
        setupButtons(view)

    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        ChikiChikiDatabaseRepository.get().getHistorySmall().observe(viewLifecycleOwner) {
            recyclerAdapter = HistoryHorizontalAdapter(it)
            recyclerAdapter.setHistoryViewClickListener(this)
            recyclerView.adapter = recyclerAdapter


        }

    }

    private fun setupButtons(view: View) {
        val supportBtn: Button = view.findViewById(R.id.library_support_btn)
        val historyBtn: Button = view.findViewById(R.id.library_history_btn)
        val watchLaterBtn: Button = view.findViewById(R.id.library_watchLater_btn)

        supportBtn.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().apply {
                setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                add(R.id.main_fragment_container, SupportFragment())
                commit()
            }
        }

        historyBtn.setOnClickListener {

            requireActivity().supportFragmentManager.beginTransaction().apply {
                setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                add(R.id.main_fragment_container, HistoryFragment())
                commit()
            }

        }

        watchLaterBtn.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().apply {
                setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                add(R.id.main_fragment_container, WatchLaterFragment())
                commit()
            }
        }


    }

    companion object {
        fun newInstance(): LibraryFragment {
            val args = Bundle()

            val fragment = LibraryFragment()
            fragment.arguments = args
            return fragment
        }
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
            setCustomAnimations(R.anim.slide_up, 0)
            replace(
                R.id.video_container,
                VideoPlayerFragment.newInstance(
                    videoId,
                    videoName,
                    videoDescription,
                    previewPath,
                    duration
                )
            )
            commit()
        }
    }

}