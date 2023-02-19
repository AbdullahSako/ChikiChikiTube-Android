package tube.chikichiki.sako.tv.other

import androidx.fragment.app.Fragment
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.Row
import tube.chikichiki.sako.tv.fragment.ChannelVideosTvFragment
import tube.chikichiki.sako.tv.fragment.Channel_HEADER_ID_1
import tube.chikichiki.sako.tv.fragment.Channel_HEADER_ID_2
import tube.chikichiki.sako.tv.fragment.PlaylistTvFragment

class ChannelPlayListTvFragmentFactory(
    backgroundManager: BackgroundManager,
    private val channelHandle: String?,
    private val channelId: Int?,
    private val displayName: String
) : BrowseSupportFragment.FragmentFactory<Fragment>() {

    private val mBackgroundManager = backgroundManager

    override fun createFragment(row: Any?): Fragment {
        val r = row as Row
        mBackgroundManager.drawable = null

        when (r.headerItem.id) {

            Channel_HEADER_ID_1 -> {
                return ChannelVideosTvFragment.newInstance(channelHandle, displayName)

            }
            Channel_HEADER_ID_2 -> {
                if (channelId != null) {
                    return PlaylistTvFragment.newInstance(channelId, displayName)
                }
            }

        }

        throw IllegalArgumentException(String.format("Invalid Row %s", row))
    }

}