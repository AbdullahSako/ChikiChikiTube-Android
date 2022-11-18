package tube.chikichiki.sako.tv.presenter

import android.view.ViewGroup
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.Row
import androidx.leanback.widget.RowPresenter

class CustomListRowPresenter:ListRowPresenter() {
    init {
        headerPresenter = CustomRowHeaderPresenter()
    }


}


