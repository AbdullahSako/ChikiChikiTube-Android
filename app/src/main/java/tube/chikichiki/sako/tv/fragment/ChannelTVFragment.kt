package tube.chikichiki.sako.tv.fragment

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.BrowseSupportFragment.MainFragmentAdapterProvider
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import tube.chikichiki.sako.R
import tube.chikichiki.sako.api.ChikiFetcher
import tube.chikichiki.sako.model.VideoChannel
import tube.chikichiki.sako.tv.presenter.ChannelTvPresenter
import tube.chikichiki.sako.tv.presenter.CustomListRowPresenter
import tube.chikichiki.sako.viewModel.ChannelViewModel

class ChannelTVFragment : BrowseSupportFragment(), MainFragmentAdapterProvider {
    private lateinit var mRowsAdapter: ArrayObjectAdapter
    private var channelViewModel: ChannelViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.headersState = BrowseSupportFragment.HEADERS_DISABLED
        channelViewModel = activity?.let { ViewModelProvider(it).get(ChannelViewModel::class.java) }

        loadAndShowChannels()


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //move channels a bit to the right
        val browseContainer =
            this.view?.findViewById<FrameLayout>(androidx.leanback.R.id.browse_container_dock)
        val metrics = resources.displayMetrics

        val padLeft = (metrics.widthPixels / 30).toInt()
        browseContainer.apply {
            browseContainer?.setPadding(padLeft, 0, 0, 0)
        }


    }

    private fun loadAndShowChannels() {
        mRowsAdapter = ArrayObjectAdapter(CustomListRowPresenter())
        val cardPresenter = ChannelTvPresenter()

        channelViewModel?.channelItemLiveData?.observe(this) {

            val channels = sortChannels(it)

            channels.forEach { videoChannel ->
                val listRowAdapter = ArrayObjectAdapter(cardPresenter)
                listRowAdapter.add(videoChannel)
                val header = HeaderItem(mRowsAdapter.size().toLong(), videoChannel.displayName)
                mRowsAdapter.add(ListRow(header, listRowAdapter))

            }



            progressBarManager.hide()

        }
        progressBarManager.show()
        adapter = mRowsAdapter


    }


    private fun sortChannels(channels: List<VideoChannel>): List<VideoChannel> {

        val sortedChannels = arrayOf(
            "gakinotsukai",
            "gottsueekanji",
            "knightscoop",
            "suiyoubinodowntown",
            "documental",
            "lincoln",
            "downtownnow",
            "worlddowntown",
            "heyheyhey",
            "matsumotoke",
            "ashitagaarusa",
            "mhk",
            "suberanaihanashi",
            "visualbum",
            "hitoshimatsumotostore"
        )
        val temp: MutableList<Pair<Int, VideoChannel>> = mutableListOf()
        val leftOver = mutableListOf<VideoChannel>()

        //sort channels based on sorted channels array
        channels.forEach {
            val index = sortedChannels.indexOf(it.channelHandle)
            if (index != -1) {
                temp.add(index to it)
            } else {
                leftOver.add(it)
            }
        }

        temp.sortBy { it.first }
        leftOver.forEach { leftOverListItem -> temp.add(channels.size to leftOverListItem) }

        val sorted: MutableList<VideoChannel> = mutableListOf()

        temp.forEach { sorted.add(it.second) }


        //remove empty channels
        return sorted.filter { it.channelHandle != "root_channel" && it.channelHandle != "fearfulkyochan" && it.channelHandle != "chikichikitube" }

    }

    companion object {
        fun newInstance() = ChannelTVFragment()
    }

    override fun getMainFragmentAdapter(): MainFragmentAdapter<*> {
        return MainFragmentAdapter(this)
    }

}