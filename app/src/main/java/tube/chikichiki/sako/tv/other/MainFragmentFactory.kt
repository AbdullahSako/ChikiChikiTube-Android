package tube.chikichiki.sako.tv.other

import androidx.fragment.app.Fragment
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.Row
import tube.chikichiki.sako.tv.fragment.*


class MainFragmentFactory(backgroundManager: BackgroundManager) :
    BrowseSupportFragment.FragmentFactory<Fragment>() {

    private val mBackgroundManager = backgroundManager


    override fun createFragment(row: Any?): Fragment {
        val r = row as Row
        mBackgroundManager.drawable = null

        when (r.headerItem.id) {
            HEADER_ID_1 -> {
                return ChannelTVFragment()
            }
            HEADER_ID_2 -> {
                return MostViewedVideosTvFragment()
            }
            HEADER_ID_3 -> {
                return RecentVideosTvFragment()
            }
            HEADER_ID_4 -> {
                return HistoryTvFragment()
            }
            HEADER_ID_5 -> {
                return WatchLaterTvFragment()
            }
            HEADER_ID_6 -> {
                return SupportTvFragmentFragment()
            }
        }

        throw IllegalArgumentException(String.format("Invalid Row %s", row))
    }

}
