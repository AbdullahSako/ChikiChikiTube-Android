package tube.chikichiki.sako.tv.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import tube.chikichiki.sako.R
import tube.chikichiki.sako.api.ChikiFetcher
import tube.chikichiki.sako.model.Video
import tube.chikichiki.sako.tv.activity.TVVideoPlayerActivity
import tube.chikichiki.sako.tv.presenter.VideoTvPresenter

private const val ARG_CHANNEL_HANDLE = "CHANNELHANDLE"
private const val ARG_CHANNEL_DISPLAY_NAME="CHANNELDISPLAYNAME"
class ChannelVideosTvFragment: VerticalGridSupportFragment(),
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


    private fun setupUi(){

        val gridPresenter = VerticalGridPresenter(ZOOM_FACTOR)
        gridPresenter.numberOfColumns = 3
        this.onItemViewClickedListener = this
        this.setOnItemViewSelectedListener(this)
        setGridPresenter(gridPresenter)

        //set title
        title = arguments?.getString(ARG_CHANNEL_DISPLAY_NAME)

        mGridAdapter = ArrayObjectAdapter(VideoTvPresenter())
        adapter =mGridAdapter
        prepareEntranceTransition()



        loadAndShowChannelVideos(arguments?.getString(ARG_CHANNEL_HANDLE))


    }

    private fun loadAndShowChannelVideos(channelHandle: String?){

        if (channelHandle != null) {
            ChikiFetcher().fetchVideosOfaChannel(channelHandle).observe(this){
                mGridAdapter.addAll(mGridAdapter.size(),it)
                startEntranceTransition()
            }
        }

    }

    private fun setTitleFontAndColor(){
        val textView=view?.findViewById<TextView>(androidx.leanback.R.id.title_text)
        textView?.setTextColor(ContextCompat.getColor(requireActivity(), R.color.font_pink))
        textView?.typeface = ResourcesCompat.getFont(requireActivity(), R.font.mochiypoppone)

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
        fun newInstance(channelHandle: String?,displayName:String): ChannelVideosTvFragment {
            val args = Bundle()
            args.putString(ARG_CHANNEL_HANDLE, channelHandle)
            args.putString(ARG_CHANNEL_DISPLAY_NAME,displayName)
            val fragment = ChannelVideosTvFragment()
            fragment.arguments = args
            return fragment
        }
    }
}