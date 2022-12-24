package tube.chikichiki.sako.tv.fragment

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.BrowseSupportFragment.MainFragmentAdapterProvider
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.ViewModelProvider
import tube.chikichiki.sako.R
import tube.chikichiki.sako.api.ChikiFetcher
import tube.chikichiki.sako.model.Video
import tube.chikichiki.sako.tv.activity.TVVideoPlayerActivity
import tube.chikichiki.sako.tv.presenter.VideoTvPresenter
import tube.chikichiki.sako.viewModel.MostViewedVideosViewModel

private const val ARG_PLAYLIST_ID= "PLAYLISTID"

class PlaylistVideosTvFragment: VerticalGridSupportFragment(), OnItemViewClickedListener,
    OnItemViewSelectedListener  {

    private lateinit var grainAnimation: AnimationDrawable
    private lateinit var mGridAdapter: ArrayObjectAdapter
    private val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_MEDIUM


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupUi()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val container = this.view?.findViewById<FrameLayout>(androidx.leanback.R.id.browse_grid_dock)
        container.apply {

            //set animation to background
            container?.background = ContextCompat.getDrawable(requireActivity(), R.drawable.grain_animation)
            grainAnimation= this?.background as AnimationDrawable
        }
        grainAnimation.start()

    }

    private fun setupUi(){
        val gridPresenter = VerticalGridPresenter(ZOOM_FACTOR)
        gridPresenter.numberOfColumns = 3
        this.onItemViewClickedListener = this
        this.setOnItemViewSelectedListener(this)
        setGridPresenter(gridPresenter)

        mGridAdapter = ArrayObjectAdapter(VideoTvPresenter())
        adapter =mGridAdapter
        prepareEntranceTransition()

        val playListId = arguments?.getInt(ARG_PLAYLIST_ID)

        loadAndShowPlaylistVideos(playListId)


    }

    private fun loadAndShowPlaylistVideos(playlistId: Int?){

            if(playlistId !=null){
                ChikiFetcher().fetchVideosOfaPlaylist(playlistId).observe(this) {

                    mGridAdapter.addAll(mGridAdapter.size(), it)
                    startEntranceTransition()
                }
            }
             


        

    }


    override fun onItemClicked(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {

        progressBarManager.show()
        val video = item as Video
        ChikiFetcher().fetchStreamingPlaylist(video.uuid).observe(this){
            progressBarManager.hide()
            val intent = TVVideoPlayerActivity.newInstance(activity,video.uuid.toString(),video.name,video.description,video.previewPath,video.duration)
            startActivity(intent)
        }

    }

    override fun onItemSelected(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {
    }
    
    companion object{
        fun newInstance(playlistId: Int): PlaylistVideosTvFragment {
            val args = Bundle()
            args.putInt(ARG_PLAYLIST_ID,playlistId)
            val fragment = PlaylistVideosTvFragment()
            fragment.arguments = args
            return fragment
        }
    }
    
}