package tube.chikichiki.sako.tv.presenter

import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import tube.chikichiki.sako.model.VideoPlaylist
import tube.chikichiki.sako.tv.view.PlaylistCardView

class VideoPlayListTvPresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        val view: PlaylistCardView? = parent?.context?.let { PlaylistCardView(it) }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {

        val playlist = item as VideoPlaylist
        val cardView = viewHolder?.view as PlaylistCardView
        cardView.updateUi(playlist)

    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

    }

}