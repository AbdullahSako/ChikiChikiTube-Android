package tube.chikichiki.sako.tv.presenter

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.RowHeaderPresenter
import tube.chikichiki.sako.R

class ChannelRowHeaderPresenter : RowHeaderPresenter() {

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder?, item: Any?) {
        val titleView = viewHolder?.view?.findViewById<TextView>(androidx.leanback.R.id.row_header)

        titleView?.setTypeface(ResourcesCompat.getFont(titleView.context, R.font.mochiypoppone))
        titleView?.setTextColor(ContextCompat.getColor(titleView.context, R.color.font_pink))

        super.onBindViewHolder(viewHolder, item)

    }

}