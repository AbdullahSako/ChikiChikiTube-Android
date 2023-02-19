package tube.chikichiki.sako.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.google.android.exoplayer2.ui.StyledPlayerView


class CustomExoPlayerView(context: Context, attr: AttributeSet? = null) :
    StyledPlayerView(context, attr) {


    override fun onTouchEvent(event: MotionEvent): Boolean {
        //allows dragging the player in motion layout
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

                if (isControllerFullyVisible) {
                    hideController()
                } else {
                    showController()
                }

            }
        }
        return false
    }


}