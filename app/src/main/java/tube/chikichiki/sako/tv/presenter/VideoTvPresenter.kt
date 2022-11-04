package tube.chikichiki.sako.tv.presenter

import android.util.Log
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import tube.chikichiki.sako.R
import tube.chikichiki.sako.model.Video

class VideoTvPresenter:Presenter() {
    private var selectedBackgroundColor:Int = 0
    private var defaultBackgroundColor:Int = 0


    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {

        parent?.context?.let {
            selectedBackgroundColor = ContextCompat.getColor(it, R.color.light_dark_grey)
            defaultBackgroundColor = ContextCompat.getColor(it, R.color.dark_grey)
        }

        val cardView = object:ImageCardView(parent?.context){
            override fun setSelected(selected: Boolean) {
                updateCardBackgroundColor(this,selected)
                super.setSelected(selected)
            }
        }


        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true

        updateCardBackgroundColor(cardView,false)
        return ViewHolder(cardView)

    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        val video = item as Video
        val cardView = viewHolder?.view as ImageCardView


        cardView.titleText = video.name

        Glide.with(cardView.context).load(video.getFullThumbnailPath()).centerCrop().format(
            DecodeFormat.PREFER_RGB_565).into(cardView.mainImageView)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        val cardView= viewHolder?.view as ImageCardView

        cardView.badgeImage = null
        cardView.mainImage = null
    }

    private fun updateCardBackgroundColor(view: ImageCardView,selected :Boolean){
        val color = if(selected) selectedBackgroundColor else defaultBackgroundColor

        view.setBackgroundColor(color)
        view.setInfoAreaBackgroundColor(color)
    }


}