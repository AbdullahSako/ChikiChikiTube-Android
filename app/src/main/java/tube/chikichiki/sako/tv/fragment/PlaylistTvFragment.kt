package tube.chikichiki.sako.tv.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import tube.chikichiki.sako.R
import tube.chikichiki.sako.Utils
import tube.chikichiki.sako.api.ChikiFetcher
import tube.chikichiki.sako.model.VideoPlaylist
import tube.chikichiki.sako.tv.activity.TVPlaylistVideosActivity
import tube.chikichiki.sako.tv.presenter.VideoPlayListTvPresenter

private const val ARG_CHANNEL_ID = "CHANNELID"
private const val TAG_ADDED_FRAGMENT = "PLAYLISTVIDEOS"
private const val ARG_CHANNEL_DISPLAY_NAME = "CHANNELDISPLAYNAME"

class PlaylistTvFragment : VerticalGridSupportFragment(),
    BrowseSupportFragment.MainFragmentAdapterProvider, OnItemViewClickedListener,
    OnItemViewSelectedListener {

    private lateinit var mGridAdapter: ArrayObjectAdapter
    private val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_MEDIUM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupUi()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitleFontAndColor()

    }

    private fun setupUi() {

        val gridPresenter = VerticalGridPresenter(ZOOM_FACTOR)
        gridPresenter.numberOfColumns = 3
        this.onItemViewClickedListener = this
        this.setOnItemViewSelectedListener(this)
        setGridPresenter(gridPresenter)

        //set title
        title = arguments?.getString(ARG_CHANNEL_DISPLAY_NAME)

        mGridAdapter = ArrayObjectAdapter(VideoPlayListTvPresenter())
        adapter = mGridAdapter
        prepareEntranceTransition()






        loadAndShowPlaylists(arguments?.getInt(ARG_CHANNEL_ID))


    }

    private fun loadAndShowPlaylists(channelId: Int?, startNumber: Int = 0) {

        if (channelId != null) {
            ChikiFetcher().fetchPlaylists(startNumber).observe(this) {
                val channelPlaylists = Utils.playlistsOfChannel(it, channelId)

                if (channelPlaylists.isEmpty()) {
                    showNoPlaylistTextView()
                }

                mGridAdapter.addAll(mGridAdapter.size(), channelPlaylists)

                if (it.size == 100) {
                    //fetch next 100 playlists
                    loadAndShowPlaylists(channelId, 100)
                } else {
                    startEntranceTransition()
                }

            }
        }

    }


    private fun setTitleFontAndColor() {
        val textView = view?.findViewById<TextView>(androidx.leanback.R.id.title_text)
        textView?.setTextColor(ContextCompat.getColor(requireActivity(), R.color.font_pink))
        textView?.typeface = ResourcesCompat.getFont(requireActivity(), R.font.mochiypoppone)

    }

    private fun showNoPlaylistTextView() {
        val textView = TextView(activity)
        textView.text = getString(R.string.no_channel_playlists_found)
        textView.setTextColor(ContextCompat.getColor(requireActivity(), R.color.orange))
        textView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply { gravity = Gravity.CENTER }
        textView.textSize = 24f
        textView.typeface = ResourcesCompat.getFont(requireActivity(), R.font.mochiypoppone)

        view?.findViewById<FrameLayout>(androidx.leanback.R.id.browse_grid_dock)?.addView(textView)
    }


    override fun getMainFragmentAdapter(): BrowseSupportFragment.MainFragmentAdapter<*> {
        return BrowseSupportFragment.MainFragmentAdapter(this)
    }

    override fun onItemClicked(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {
        val playlist = item as VideoPlaylist

        startActivity(
            TVPlaylistVideosActivity.newIntent(
                requireActivity(),
                playlist.id,
                playlist.displayName
            )
        )

    }


    override fun onItemSelected(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {

    }

    companion object {
        fun newInstance(channelId: Int, displayName: String): PlaylistTvFragment {
            val args = Bundle()
            args.putInt(ARG_CHANNEL_ID, channelId)
            args.putString(ARG_CHANNEL_DISPLAY_NAME, displayName)
            val fragment = PlaylistTvFragment()
            fragment.arguments = args
            return fragment
        }
    }

}