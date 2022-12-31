package tube.chikichiki.sako.tv.presenter

import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import tube.chikichiki.sako.model.Video
import tube.chikichiki.sako.model.VideoAndWatchedTimeModel
import tube.chikichiki.sako.tv.view.VideoCardView

class VideoTvPresenter:Presenter() {


    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        val view:VideoCardView? = parent?.context?.let { VideoCardView(it) }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {

        val video = item as VideoAndWatchedTimeModel
        val cardView = viewHolder?.view as VideoCardView
        cardView.updateUi(video)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

    }
}