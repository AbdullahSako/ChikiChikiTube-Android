package tube.chikichiki.sako.tv.presenter

import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import tube.chikichiki.sako.R
import tube.chikichiki.sako.model.VideoChannel

class ChannelTvPresenter() : Presenter() {
    private var selectedBackgroundColor: Int = 0
    private var defaultBackgroundColor: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        parent?.context?.let {
            selectedBackgroundColor = ContextCompat.getColor(it, R.color.light_tv_bg)
            defaultBackgroundColor = ContextCompat.getColor(it, R.color.tv_bg)
        }
        val cardView = object : ImageCardView(parent?.context) {
            override fun setSelected(selected: Boolean) {
                updateCardBackgroundColor(this, selected)
                super.setSelected(selected)
            }
        }
        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true


        val channelImageCardView =
            cardView.findViewById<RelativeLayout>(androidx.leanback.R.id.info_field)

        //make space for content text by removing title space
        channelImageCardView.findViewById<TextView>(
            androidx.leanback.R.id.title_text
        ).height = 0

        //change max lines of context text to 3
        channelImageCardView.findViewById<TextView>(
            androidx.leanback.R.id.content_text
        ).apply {
            this.maxLines = 3
        }

        updateCardBackgroundColor(cardView, false)
        return ViewHolder(cardView)

    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        val channel = item as VideoChannel
        val cardView = viewHolder?.view as ImageCardView

        val metrics = cardView.context.resources.displayMetrics
        val width = (metrics.widthPixels / 1.2).toInt()
        val height = (metrics.heightPixels / 2.1).toInt()



        cardView.contentText = channel.description
        cardView.setMainImageDimensions(width, height)
        Glide.with(cardView.context).load(channel.banner?.getFullPath()).centerCrop()
            .format(DecodeFormat.PREFER_RGB_565).into(cardView.mainImageView)


    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        val cardView = viewHolder?.view as ImageCardView

        cardView.badgeImage = null
        cardView.mainImage = null
    }

    private fun updateCardBackgroundColor(view: ImageCardView, selected: Boolean) {
        val color = if (selected) selectedBackgroundColor else defaultBackgroundColor

        view.setBackgroundColor(color)
        view.setInfoAreaBackgroundColor(color)
    }


    companion object {

        private const val CARD_WIDTH = 1600
        private const val CARD_HEIGHT = 500
    }

}