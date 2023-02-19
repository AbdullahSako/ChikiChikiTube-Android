package tube.chikichiki.sako.tv.fragment

import android.os.Bundle
import android.view.View
import androidx.leanback.app.BrowseSupportFragment.MainFragmentAdapterProvider
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.VerticalGridView
import androidx.lifecycle.ViewModelProvider
import tube.chikichiki.sako.Utils.sortChannels
import tube.chikichiki.sako.model.VideoChannel
import tube.chikichiki.sako.tv.presenter.ChannelListRowPresenter
import tube.chikichiki.sako.tv.presenter.ChannelTvPresenter
import tube.chikichiki.sako.viewModel.ChannelViewModel

class ChannelTVFragment : RowsSupportFragment(), MainFragmentAdapterProvider {
    private lateinit var mRowsAdapter: ArrayObjectAdapter
    private var channelViewModel: ChannelViewModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        channelViewModel = activity?.let { ViewModelProvider(it).get(ChannelViewModel::class.java) }

        loadAndShowChannels()


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //move channels a bit to the right
        val browseContainer =
            this.view?.findViewById<VerticalGridView>(androidx.leanback.R.id.container_list)
        val metrics = resources.displayMetrics

        val padLeft = (metrics.widthPixels / 30).toInt()
        browseContainer.apply {
            browseContainer?.setPadding(padLeft, 0, 0, 0)
        }

        setUpListeners()


    }

    private fun loadAndShowChannels() {
        mRowsAdapter = ArrayObjectAdapter(ChannelListRowPresenter())
        val cardPresenter = ChannelTvPresenter()

        channelViewModel?.channelItemLiveData?.observe(this) {

            val channels = sortChannels(it)

            channels.forEach { videoChannel ->
                val listRowAdapter = ArrayObjectAdapter(cardPresenter)
                listRowAdapter.add(videoChannel)
                val header = HeaderItem(mRowsAdapter.size().toLong(), videoChannel.displayName)
                mRowsAdapter.add(ListRow(header, listRowAdapter))
            }


        }
        adapter = mRowsAdapter


    }

    private fun setUpListeners() {

        setOnItemViewClickedListener { itemViewHolder, item, rowViewHolder, row ->
            val channel = item as VideoChannel



            requireActivity().supportFragmentManager.beginTransaction().apply {
                add(
                    tube.chikichiki.sako.R.id.tv_fragment_container,
                    ChannelAndPlaylistParentTvFragment.newInstance(
                        channel.channelHandle,
                        channel.id,
                        channel.displayName
                    )
                )
                addToBackStack(null)
                commit()
            }

        }


    }

    override fun onResume() {
        super.onResume()

        val temp = parentFragment as MainTvFragment
        temp.showSearchOrbAndTitle()

    }

    override fun onPause() {
        super.onPause()

        val temp = parentFragment as MainTvFragment
        temp.hideSearchOrbAndTitle()

    }


    companion object {
        fun newInstance() = ChannelTVFragment()
    }

}