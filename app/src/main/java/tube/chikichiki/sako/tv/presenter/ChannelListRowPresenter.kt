package tube.chikichiki.sako.tv.presenter

import androidx.leanback.widget.ListRowPresenter

class ChannelListRowPresenter : ListRowPresenter() {
    init {
        headerPresenter = ChannelRowHeaderPresenter()
    }


}


