package tube.chikichiki.sako.tv.presenter

import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import tube.chikichiki.sako.model.WatchLater
import tube.chikichiki.sako.tv.view.VideoCardView

class WatchLaterTvPresenter : Presenter() {


    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        val view: VideoCardView? = parent?.context?.let { VideoCardView(it) }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {

        val watchLater = item as WatchLater
        val cardView = viewHolder?.view as VideoCardView
        cardView.updateUi(watchLater)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

    }

}