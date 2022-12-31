package tube.chikichiki.sako.tv.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import tube.chikichiki.sako.R
import tube.chikichiki.sako.tv.fragment.PlaylistVideosTvFragment
import tube.chikichiki.sako.tv.fragment.SearchFragment

class TvSearchActivity:FragmentActivity(R.layout.activity_tv_search) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


            supportFragmentManager.beginTransaction().apply {
                replace(
                    R.id.tv_search_container,
                    SearchFragment()
                )
                commit()
        }
    }

}