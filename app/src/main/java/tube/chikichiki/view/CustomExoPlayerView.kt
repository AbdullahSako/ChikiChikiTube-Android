package tube.chikichiki.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.google.android.exoplayer2.ui.StyledPlayerView

class CustomExoPlayerView(context: Context,attr:AttributeSet?=null): StyledPlayerView(context,attr) {


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action){
            MotionEvent.ACTION_DOWN->{

                if(isControllerFullyVisible) {
                    hideController()
                }
                else{
                    showController()
                }

            }
        }
        return false
    }


}