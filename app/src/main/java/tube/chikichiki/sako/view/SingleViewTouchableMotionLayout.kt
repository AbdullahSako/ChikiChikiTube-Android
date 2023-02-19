package tube.chikichiki.sako.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import tube.chikichiki.sako.R

class SingleViewTouchableMotionLayout(context: Context, attr: AttributeSet? = null) :
    MotionLayout(context, attr) {

    private var motionLayout: MotionLayout =
        LayoutInflater.from(context)
            .inflate(R.layout.video_player_motion_layout, this, false) as MotionLayout
    private val touchableArea: View


    init {
        addView(motionLayout)

        touchableArea = motionLayout.findViewById(R.id.constraintLayout)


    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        val isInProgress = (motionLayout.progress > 0.0f && motionLayout.progress < 1.0f)
        val isInTarget = event?.let { touchEventInsideTargetView(touchableArea, it) }
        return if (isInProgress || isInTarget == true) {
            return super.onInterceptTouchEvent(event)
        } else {
            true
        }

    }


    private fun touchEventInsideTargetView(v: View, ev: MotionEvent): Boolean {
        if (ev.x > v.left && ev.x < v.right) {
            if (ev.y > v.top && ev.y < v.bottom) {
                return true
            }
        }
        return false

    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return false
    }
}