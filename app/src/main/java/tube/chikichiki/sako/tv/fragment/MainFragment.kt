package tube.chikichiki.sako.tv.fragment

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import tube.chikichiki.sako.R
import tube.chikichiki.sako.tv.other.MainFragmentFactory
import java.lang.Thread.sleep

const val HEADER_ID_1: Long = 1
const val HEADER_NAME_1 = "Shows"
const val HEADER_ID_2: Long = 2
const val HEADER_NAME_2 = "Most Viewed"
const val HEADER_ID_3: Long = 3
const val HEADER_NAME_3 = "Recent"
const val HEADER_ID_4: Long = 4
const val HEADER_NAME_4 = "Library"

class PageAndListRowFragment:BrowseSupportFragment() {


    private lateinit var grainAnimation: AnimationDrawable
    private lateinit var backgroundManager:BackgroundManager
    private lateinit var mRowsAdapter:ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUi()
        loadData()


        mainFragmentRegistry.registerFragment(PageRow::class.java,MainFragmentFactory(backgroundManager))

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val browseContainer = this.view?.findViewById<FrameLayout>(androidx.leanback.R.id.browse_container_dock)
        browseContainer.apply {

            //set animation to background
            browseContainer?.background = ContextCompat.getDrawable(requireActivity(),R.drawable.grain_animation)
            grainAnimation= this?.background as AnimationDrawable
        }
        grainAnimation.start()

    }


    private fun setupUi(){
        backgroundManager =BackgroundManager.getInstance(activity)
        backgroundManager.attach(activity?.window)

        this.headersState = BrowseSupportFragment.HEADERS_ENABLED
        this.isHeadersTransitionOnBackEnabled = false
        this.brandColor = ContextCompat.getColor(requireActivity(), R.color.tv_bg)

        this.setOnSearchClickedListener {
            Toast.makeText(activity,"SEARCH",Toast.LENGTH_SHORT).show()
        }

        prepareEntranceTransition()
    }

    private fun loadData(){
        mRowsAdapter= ArrayObjectAdapter(ListRowPresenter())
        this.adapter = mRowsAdapter

        Thread{
            sleep(1000)
            createRows()
            startEntranceTransition()
        }.start()

        progressBarManager.hide()
    }

    private fun createRows(){
        val headerItem1 = HeaderItem(HEADER_ID_1,HEADER_NAME_1)
        val pageRow1 = PageRow(headerItem1)
        mRowsAdapter.add(pageRow1)

        val headerItem2 = HeaderItem(HEADER_ID_2,HEADER_NAME_2)
        val pageRow2 = PageRow(headerItem2)
        mRowsAdapter.add(pageRow2)


        val headerItem3 = HeaderItem(HEADER_ID_3,HEADER_NAME_3)
        val pageRow3 = PageRow(headerItem3)
        mRowsAdapter.add(pageRow3)


        val headerItem4 = HeaderItem(HEADER_ID_4,HEADER_NAME_4)
        val pageRow4 = PageRow(headerItem4)
        mRowsAdapter.add(pageRow4)

    }























}
