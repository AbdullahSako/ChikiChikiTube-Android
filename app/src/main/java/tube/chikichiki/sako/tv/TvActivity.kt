package tube.chikichiki.sako.tv

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.FragmentActivity
import tube.chikichiki.sako.R
import tube.chikichiki.sako.tv.fragment.ChannelTVFragment
import tube.chikichiki.sako.tv.fragment.PageAndListRowFragment

class TvActivity:FragmentActivity(R.layout.tv_activity_tv) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.tv_fragment_container,PageAndListRowFragment())
            commit()
        }

    }

}