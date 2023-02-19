package tube.chikichiki.sako.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2

import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import tube.chikichiki.sako.R
import tube.chikichiki.sako.adapter.PagerAdapter

private const val EXTRA_CHANNEL_ID = "CHANNELID"
private const val EXTRA_CHANNEL_HANDLE = "CHANNELHANDLE"

class ChannelFragment : Fragment(R.layout.fragment_channel) {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var fragmentList: List<Fragment>
    private lateinit var pagerAdapter: PagerAdapter

    private val tabs: ArrayList<String> by lazy {
        arrayListOf(getString(R.string.videos), getString(R.string.playlists))
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewPager = view.findViewById(R.id.pager)
        tabLayout = view.findViewById(R.id.channel_tab_layout)
        val channelId = arguments?.getInt(EXTRA_CHANNEL_ID) as Int
        val channelHandle = arguments?.getString(EXTRA_CHANNEL_HANDLE) as String


        fragmentList = prepareFragmentsList(channelId, channelHandle)

        setUpAdapter(fragmentList)
        setUpPager()
        prepareTabView()

    }


    private fun setUpAdapter(list: List<Fragment>) {
        val fm = childFragmentManager
        val lifecycle = viewLifecycleOwner.lifecycle
        pagerAdapter = PagerAdapter(fm, lifecycle, list)
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
            ChannelVideosFragment().apply {
                arguments = ChannelVideosFragment.getVideoArgsBundle(channelHandle)
            },
            PlaylistFragment().apply {
                arguments = PlaylistFragment.getNavArgsBundle(channelId)
            }

        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        //remove fragment on back press
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                //re enable transition to start
                activity?.findViewById<MotionLayout>(R.id.activity_main_motion_layout)
                    ?.setTransition(R.id.main_start, R.id.main_end)
                activity?.findViewById<MotionLayout>(R.id.activity_main_motion_layout)
                    ?.transitionToEnd()


                parentFragmentManager.beginTransaction().apply {
                    remove(this@ChannelFragment).commit()

                }
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }


    override fun onDestroy() {
        super.onDestroy()

        //bring back toolbar and bottom nav bar
        activity?.findViewById<MotionLayout>(R.id.activity_main_motion_layout)?.transitionToStart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewPager.adapter = null
    }


    companion object {
        fun newInstance(channelId: Int, channelHandle: String): ChannelFragment {
            return ChannelFragment().apply {
                arguments =
                    bundleOf(EXTRA_CHANNEL_ID to channelId, EXTRA_CHANNEL_HANDLE to channelHandle)
            }
        }
    }

}