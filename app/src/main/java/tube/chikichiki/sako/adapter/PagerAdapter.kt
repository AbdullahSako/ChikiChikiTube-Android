package tube.chikichiki.sako.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapter(fm: FragmentManager,lifeCycle:Lifecycle, private val list: List<Fragment>) :
    FragmentStateAdapter(fm,lifeCycle) {

    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return list[position]
    }

}