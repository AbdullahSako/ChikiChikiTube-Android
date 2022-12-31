package tube.chikichiki.sako.tv.other

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.leanback.widget.TitleViewAdapter
import tube.chikichiki.sako.R


/**
 * Custom title view to be used in [android.support.v17.leanback.app.BrowseFragment].
 */
class CustomTitleView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) :
    RelativeLayout(context, attrs, defStyle), TitleViewAdapter.Provider {
    private lateinit var mTitleView: TextView
    private lateinit var mSearchOrbView: View
    private val mTitleViewAdapter: TitleViewAdapter = object : TitleViewAdapter() {
        override fun getSearchAffordanceView(): View {
            return mSearchOrbView
        }

        override fun setTitle(titleText: CharSequence) {
            this@CustomTitleView.setTitle(titleText)
        }

        override fun setBadgeDrawable(drawable: Drawable) {
            //CustomTitleView.this.setBadgeDrawable(drawable);
        }

        override fun setOnSearchClickedListener(listener: OnClickListener) {
            mSearchOrbView.setOnClickListener(listener)
        }

        override fun updateComponentsVisibility(flags: Int) {
            /*if ((flags & BRANDING_VIEW_VISIBLE) == BRANDING_VIEW_VISIBLE) {
                updateBadgeVisibility(true);
            } else {
                mAnalogClockView.setVisibility(View.GONE);
                mTitleView.setVisibility(View.GONE);
            }*/
            val visibility =
                if (flags and SEARCH_VIEW_VISIBLE == SEARCH_VIEW_VISIBLE) VISIBLE else INVISIBLE
            mSearchOrbView.visibility = visibility
        }

        private fun updateBadgeVisibility(visible: Boolean) {
            if (visible) {
                mTitleView.visibility = VISIBLE
            } else {
                mTitleView.visibility = GONE
            }
        }
    }

    init {
        val root: View = LayoutInflater.from(context).inflate(R.layout.layout_tv_custom_channel_videos_title_view, this)
        mTitleView = root.findViewById<View>(R.id.title_text) as TextView
        mSearchOrbView = root.findViewById(R.id.title_orb)
    }

    fun setTitle(title: CharSequence?) {
        if (title != null) {
            mTitleView.text = title
            mTitleView.visibility = VISIBLE
        }
    }

    fun setBadgeDrawable(drawable: Drawable?) {
        if (drawable != null) {
            mTitleView.visibility = GONE
        }
    }

    override fun getTitleViewAdapter(): TitleViewAdapter {
        return mTitleViewAdapter
    }
}