package tube.chikichiki.sako.fragment

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import tube.chikichiki.sako.R
import tube.chikichiki.sako.Utils
import tube.chikichiki.sako.adapter.VideoAdapter
import tube.chikichiki.sako.api.ChikiFetcher
import tube.chikichiki.sako.database.ChikiChikiDatabaseRepository
import tube.chikichiki.sako.model.Video
import java.util.*

private const val ARG_PLAYLIST_ID = "PLAYLISTID"

class PlaylistVideosFragment : Fragment(R.layout.fragment_playlist_videos),
    VideoAdapter.VideoViewClick {
    private lateinit var grainAnimation: AnimationDrawable
    private lateinit var playlistVideosRecyclerView: RecyclerView
    private lateinit var videoAdapter: VideoAdapter
    private lateinit var currentListOfVideos: List<Video>
    private var playlistId: Int? = null
    private var isLoading = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val constraint: ConstraintLayout = view.findViewById(R.id.playlist_videos_constraint_layout)
        playlistVideosRecyclerView = view.findViewById(R.id.playlist_videos_recycler_view)
        val noVideosTextview: TextView = view.findViewById(R.id.no_playlist_videos_found_text_view)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)


        //get playlist id from fragment arguments
        playlistId = arguments?.getInt(ARG_PLAYLIST_ID)

        //set up recycler view layout manager and adapter
        playlistVideosRecyclerView.layoutManager = LinearLayoutManager(context)
        videoAdapter = VideoAdapter()
        playlistVideosRecyclerView.adapter = videoAdapter

        //retrieve playlist videos from api
        playlistId?.let {
            ChikiFetcher().fetchVideosOfaPlaylist(it).observe(
                viewLifecycleOwner
            ) { list ->

                ChikiChikiDatabaseRepository.get().getAllWatchedVideos()
                    .observe(viewLifecycleOwner) {

                        //apply recycler view adapter with retrieved list
                        playlistVideosRecyclerView.apply {

                            videoAdapter.submitList(Utils.getPairOfVideos(list, it))
                            videoAdapter.setVideoViewClickListener(this@PlaylistVideosFragment)
                        }

                        currentListOfVideos = list

                        //if there are no videos for the channel show text view
                        if (list.isEmpty()) {
                            noVideosTextview.visibility = View.VISIBLE
                        }
                        //hide loading bar after loading list
                        progressBar.visibility = View.GONE
                    }
            }
        }


        //retrieve more videos from api if scrolled down far enough in recycler view
        playlistVideosRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                if (linearLayoutManager.findLastVisibleItemPosition() == currentListOfVideos.size - 20) {

                    if (!isLoading) {

                        loadMore()
                        isLoading = true
                    }

                }
            }
        })

        //set fragment background animation and start it
        constraint.apply {
            setBackgroundResource(R.drawable.grain_animation)
            grainAnimation = background as AnimationDrawable
        }
        grainAnimation.start()
    }

    // retrieve videos from api based on current list size as a page start point
    private fun loadMore() {
        playlistId?.let {
            ChikiFetcher().fetchVideosOfaPlaylist(it, currentListOfVideos.size).observe(
                viewLifecycleOwner
            ) { list ->
                ChikiChikiDatabaseRepository.get().getAllWatchedVideos()
                    .observe(viewLifecycleOwner) { watchedTime ->
                        currentListOfVideos =
                            currentListOfVideos + list //add lists to get all available videos size
                        videoAdapter.submitList(
                            Utils.getPairOfVideos(
                                currentListOfVideos,
                                watchedTime
                            )
                        ) //load new videos in recyclerview
                        isLoading = false
                    }


            }
        }
    }

    override fun onVideoClick(
        videoId: UUID,
        videoName: String,
        videoDescription: String,
        previewPath: String,
        duration: Int
    ) {


        activity?.supportFragmentManager?.beginTransaction()?.apply {
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
        //remove fragment on back press
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                parentFragmentManager.beginTransaction().remove(this@PlaylistVideosFragment)
                    .commit()
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }


    override fun onStart() {
        super.onStart()

        //disable view pager in channel activity since its host activity
        activity?.findViewById<ViewPager2>(R.id.pager)?.isUserInputEnabled = false


    }

    override fun onStop() {
        super.onStop()

        //enable view pager in channel activity
        activity?.findViewById<ViewPager2>(R.id.pager)?.isUserInputEnabled = true


    }


    companion object {
        fun newInstance(playlistId: Int): PlaylistVideosFragment {
            return PlaylistVideosFragment().apply {
                arguments = bundleOf(ARG_PLAYLIST_ID to playlistId)
            }
        }
    }
}