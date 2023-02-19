package tube.chikichiki.sako.tv.fragment

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.PageRow
import tube.chikichiki.sako.R
import tube.chikichiki.sako.tv.other.ChannelPlayListTvFragmentFactory


const val Channel_HEADER_ID_1: Long = 1
const val Channel_HEADER_NAME_1 = "Videos"
const val Channel_HEADER_ID_2: Long = 2
const val Channel_HEADER_NAME_2 = "Playlists"

private const val ARG_CHANNEL_HANDLE = "CHANNELHANDLE"
private const val ARG_CHANNEL_ID = "CHANNELID"
private const val ARG_CHANNEL_DISPLAY_NAME = "DISPLAYNAME"

class ChannelAndPlaylistParentTvFragment : BrowseSupportFragment() {

    private lateinit var grainAnimation: AnimationDrawable
    private lateinit var backgroundManager: BackgroundManager
    private lateinit var mRowsAdapter: ArrayObjectAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupUi()
        handleOnBackPress()
        loadData()


        //get args
        val channelHandle = arguments?.getString(ARG_CHANNEL_HANDLE)
        val channelId = arguments?.getInt(ARG_CHANNEL_ID)
        val displayName = arguments?.getString(ARG_CHANNEL_DISPLAY_NAME)


        //set fragment factory
        mainFragmentRegistry.registerFragment(PageRow::class.java,
            displayName?.let {
                ChannelPlayListTvFragmentFactory(
                    backgroundManager, channelHandle, channelId,
                    it
                )
            }
        )

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpBackgroundAnimation()


    }

    private fun setupUi() {
        backgroundManager = BackgroundManager.getInstance(activity)


        this.headersState = HEADERS_HIDDEN
        this.isHeadersTransitionOnBackEnabled = false
        this.brandColor = ContextCompat.getColor(requireActivity(), R.color.tv_bg)

    }


    private fun loadData() {
        mRowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        this.adapter = mRowsAdapter


        createRows()


        progressBarManager.hide()
    }

    private fun createRows() {
        val headerItem1 = HeaderItem(Channel_HEADER_ID_1, Channel_HEADER_NAME_1)
        val pageRow1 = PageRow(headerItem1)
        mRowsAdapter.add(pageRow1)

        val headerItem2 = HeaderItem(Channel_HEADER_ID_2, Channel_HEADER_NAME_2)
        val pageRow2 = PageRow(headerItem2)
        mRowsAdapter.add(pageRow2)


    }

    private fun handleOnBackPress() {

        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {

                    requireActivity().supportFragmentManager.popBackStack(null, 0)

                }


            })


    }

    private fun setUpBackgroundAnimation() {
        val browseContainer =
            this.view?.findViewById<FrameLayout>(androidx.leanback.R.id.browse_container_dock)
        browseContainer.apply {

            //set animation to background
            browseContainer?.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.grain_animation)
            grainAnimation = this?.background as AnimationDrawable
        }
        grainAnimation.start()
    }


    companion object {
        fun newInstance(
            channelHandle: String?,
            channelId: Int,
            displayName: String
        ): ChannelAndPlaylistParentTvFragment {
            val args = Bundle()
            args.putString(ARG_CHANNEL_HANDLE, channelHandle)
            args.putInt(ARG_CHANNEL_ID, channelId)
            args.putString(ARG_CHANNEL_DISPLAY_NAME, displayName)

            val fragment = ChannelAndPlaylistParentTvFragment()
            fragment.arguments = args
            return fragment
        }
    }

}