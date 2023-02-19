package tube.chikichiki.sako.fragment

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import tube.chikichiki.sako.R
import tube.chikichiki.sako.adapter.WatchLaterAdapter
import tube.chikichiki.sako.database.ChikiChikiDatabaseRepository
import tube.chikichiki.sako.model.WatchLater
import java.util.*

class WatchLaterFragment : Fragment(R.layout.fragment_watch_later),
    WatchLaterAdapter.WatchLaterClick, WatchLaterAdapter.WatchLaterRemoveClick {
    private lateinit var grainAnimation: AnimationDrawable


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rootView: ConstraintLayout = view.findViewById(R.id.watch_later_root_view)
        val recyclerView: RecyclerView = view.findViewById(R.id.watch_later_recycler_view)

        //set fragment background animation and start it
        rootView.apply {
            setBackgroundResource(R.drawable.grain_animation)
            grainAnimation = background as AnimationDrawable
        }
        grainAnimation.start()

        setupRecyclerView(recyclerView)


    }


    private fun setupRecyclerView(recyclerView: RecyclerView) {
        val noVideosTxt: TextView? = view?.findViewById(R.id.no_videos_text_watch_later)

        recyclerView.layoutManager = LinearLayoutManager(
            activity,
            LinearLayoutManager.VERTICAL, false
        )

        ChikiChikiDatabaseRepository.get().getAllWatchLater().observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                noVideosTxt?.visibility = View.VISIBLE
            } else {
                noVideosTxt?.visibility = View.GONE
            }

            val adp = WatchLaterAdapter(it)

            //set video on click listener
            adp.setWatchLaterOnClickListener(this)

            //set remove on click listener
            adp.setWatchLaterRemoveOnClickListener(this)

            recyclerView.adapter = adp
        }


    }

    override fun onRemoveClick(watchLater: WatchLater) {
        ChikiChikiDatabaseRepository.get().removeFromWatchLater(watchLater)

        val root = view?.findViewById<ConstraintLayout>(R.id.watch_later_root_view)
        if (root != null) {
            val snack =
                Snackbar.make(root, R.string.video_removed_watch_later, Snackbar.LENGTH_SHORT)
                    .setTextColor(ContextCompat.getColor(requireActivity(), R.color.font_pink))
                    .setBackgroundTint(ContextCompat.getColor(requireActivity(), R.color.dark_grey))
                    .setAnchorView(R.id.bottomNavigationView)
            snack.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).typeface =
                ResourcesCompat.getFont(requireActivity(), R.font.mochiypoppone)
            snack.show()
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


    override fun onAttach(context: Context) {
        super.onAttach(context)

        //remove fragment on back press instead of closing activity
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                //remove support fragment
                parentFragmentManager.beginTransaction().remove(this@WatchLaterFragment).commit()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }


}