package tube.chikichiki.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import tube.chikichiki.R
import tube.chikichiki.adapter.PagerAdapter
import tube.chikichiki.fragment.ChannelVideosFragment
import tube.chikichiki.fragment.ChannelVideosFragment.Companion.getVideoArgsBundle
import tube.chikichiki.fragment.PlaylistFragment
import tube.chikichiki.fragment.PlaylistFragment.Companion.getNavArgsBundle

class ChannelActivity : AppCompatActivity() {

    private val args: ChannelActivityArgs by navArgs()

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    private lateinit var pagerAdapter: PagerAdapter

    private val tabs: ArrayList<String> by lazy {
        arrayListOf(getString(R.string.playlists), getString(R.string.videos))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel)

        viewPager = findViewById(R.id.pager)
        tabLayout = findViewById(R.id.tabLayout)

        val channelId = args.channelId
        val channelHandle = args.channelHandle

        val fragmentList = prepareFragmentsList(channelId, channelHandle)

        setUpAdapter(fragmentList)
        setUpPager()
        prepareTabView()
    }

    private fun setUpAdapter(list: List<Fragment>) {
        pagerAdapter = PagerAdapter(this, list)
    }

    private fun setUpPager() = viewPager.apply {
        adapter = pagerAdapter
    }

    private fun prepareTabView() {
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabs[position]
        }.attach()
    }

    private fun prepareFragmentsList(channelId: Int, channelHandle: String): List<Fragment> {
        return listOf(
            PlaylistFragment().apply {
                arguments = getNavArgsBundle(channelId)
            },
            ChannelVideosFragment().apply {
                arguments = getVideoArgsBundle(channelHandle)
            }
        )
    }
}