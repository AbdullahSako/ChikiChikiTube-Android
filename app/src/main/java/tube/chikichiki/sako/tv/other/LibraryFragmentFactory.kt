package tube.chikichiki.sako.tv.other

import androidx.fragment.app.Fragment
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.Row
import tube.chikichiki.sako.fragment.SupportFragment
import tube.chikichiki.sako.tv.fragment.*

class LibraryFragmentFactory(backgroundManager: BackgroundManager):BrowseSupportFragment.FragmentFactory<Fragment>() {

    private val mBackgroundManager = backgroundManager


    override fun createFragment(row: Any?): Fragment {

        val r = row as Row
        mBackgroundManager.drawable = null

        when(r.headerItem.id){
            Library_HEADER_ID_1 -> {
                return HistoryTvFragment()
            }
            Library_HEADER_ID_2 ->{
                return WatchLaterTvFragment()
            }
            Library_HEADER_ID_3 ->{
               return SupportTvFragmentFragment()
            }
        }

        throw IllegalArgumentException(String.format("Invalid Row %s",row))
    }

}