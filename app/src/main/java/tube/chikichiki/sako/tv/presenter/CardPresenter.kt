package tube.chikichiki.sako.tv.presenter

import android.R
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import tube.chikichiki.sako.model.Video


class CardPresenter : Presenter() {
    private var mSelectedBackgroundColor = -1
    private var mDefaultBackgroundColor = -1
    private var mDefaultCardImage: Drawable? = null


    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        mDefaultBackgroundColor = ContextCompat.getColor(parent!!.context, R.color.darker_gray)
        mSelectedBackgroundColor =
            ContextCompat.getColor(parent.context, R.color.background_light)

        val cardView: ImageCardView = object : ImageCardView(parent!!.context) {
            override fun setSelected(selected: Boolean) {
                updateCardBackgroundColor(this, selected)
                super.setSelected(selected)
            }
        }

        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true
        updateCardBackgroundColor(cardView, false)
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        val video: Video = item as Video

        val cardView = viewHolder!!.view as ImageCardView
        cardView.titleText = video.name
        cardView.contentText = video.description

        if (video.previewPath != null) {
            // Set card size from dimension resources.
            val res: Resources = cardView.resources
            val width: Int = 200
            val height: Int = 200
            cardView.setMainImageDimensions(width, height)
            Glide.with(cardView.context).load(video.getFullThumbnailPath())
                .apply(RequestOptions.errorOf(mDefaultCardImage)).into(cardView.mainImageView)
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        val cardView = viewHolder!!.view as ImageCardView

        // Remove references to images so that the garbage collector can free up memory.

        // Remove references to images so that the garbage collector can free up memory.
        cardView.badgeImage = null
        cardView.mainImage = null

    }


    private fun updateCardBackgroundColor(view: ImageCardView, selected: Boolean) {
        val color = if (selected) mSelectedBackgroundColor else mDefaultBackgroundColor

        // Both background colors should be set because the view's
        // background is temporarily visible during animations.
        view.setBackgroundColor(color)
    }

}