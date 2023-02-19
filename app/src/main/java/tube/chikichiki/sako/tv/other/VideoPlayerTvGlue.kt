package tube.chikichiki.sako.tv.other

import android.content.Context
import android.text.TextUtils
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.TextViewCompat
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.*
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import tube.chikichiki.sako.R

class VideoPlayerTvGlue(
    context: Context,
    adapter: LeanbackPlayerAdapter,
    private val watchLaterAction: Action
) : PlaybackTransportControlGlue<LeanbackPlayerAdapter>(context, adapter) {

    private var actionClick: ActionClick? = null
    private var actionAdapter: ArrayObjectAdapter? = ArrayObjectAdapter()


    interface ActionClick {
        fun onActionClick(action: Action?)
    }

    fun setActionClickListener(clickListener: ActionClick) {
        actionClick = clickListener
    }

    fun notifyAdapterActionChanged() {
        val index = actionAdapter?.indexOf(watchLaterAction)
        val size = actionAdapter?.size()
        if (index != null) {
            if (size != null) {
                actionAdapter?.notifyItemRangeChanged(index, size)
            }
        }
    }


    override fun onActionClicked(action: Action?) {
        when (action) {
            watchLaterAction -> {
                actionClick?.onActionClick(action)

            }
            else -> super.onActionClicked(action)
        }

    }


    override fun onCreateRowPresenter(): PlaybackRowPresenter {
        return super.onCreateRowPresenter().apply {
            val temp = (this as? PlaybackTransportRowPresenter)
            temp?.progressColor = ContextCompat.getColor(context, R.color.icon_yellow)
            temp?.setDescriptionPresenter(DescriptionPresenter(context))
        }
    }


    override fun onCreateSecondaryActions(secondaryActionsAdapter: ArrayObjectAdapter?) {
        super.onCreateSecondaryActions(secondaryActionsAdapter)
        secondaryActionsAdapter?.add(watchLaterAction)
        actionAdapter = secondaryActionsAdapter


    }


    // Customize Title and body of player
    private class DescriptionPresenter(private val context: Context) :
        AbstractDetailsDescriptionPresenter() {

        override fun onBindDescription(viewHolder: ViewHolder, item: Any) {


            val glue: VideoPlayerTvGlue = item as VideoPlayerTvGlue

            viewHolder.title.apply {
                setPadding(0, 16, 0, 0)
                text = glue.title ?: ""
            }

            //change color
            viewHolder.title.setTextColor(ContextCompat.getColor(context, R.color.font_pink))

            //change font
            viewHolder.title.typeface = ResourcesCompat.getFont(context, R.font.mochiypoppone)


            viewHolder.body.apply {
                isSingleLine = false
                ellipsize = TextUtils.TruncateAt.END
                TextViewCompat.setAutoSizeTextTypeWithDefaults(
                    this,
                    TextViewCompat.AUTO_SIZE_TEXT_TYPE_NONE
                )
                maxLines = 5
                minLines = 5
            }

        }
    }

}