package tube.chikichiki.sako.tv.activity

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import tube.chikichiki.sako.R
import tube.chikichiki.sako.tv.fragment.MainTvFragment

class TvActivity : FragmentActivity(R.layout.activity_tv_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.tv_fragment_container, MainTvFragment())
            commit()
        }

    }


}