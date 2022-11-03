package tube.chikichiki.sako.tv.fragment

import android.os.Bundle
import androidx.leanback.app.BrowseSupportFragment

class MostViewedVideosTvFragment:BrowseSupportFragment(),
    BrowseSupportFragment.MainFragmentAdapterProvider {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.headersState = BrowseSupportFragment.HEADERS_DISABLED



    }


    override fun getMainFragmentAdapter(): MainFragmentAdapter<*> {
        return MainFragmentAdapter(this)
    }
}