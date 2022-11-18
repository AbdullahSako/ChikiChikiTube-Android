package tube.chikichiki.sako.tv.fragment

import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.BrowseSupportFragment.MainFragmentAdapterProvider
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.PageRow
import tube.chikichiki.sako.R
import tube.chikichiki.sako.tv.other.LibraryFragmentFactory
import tube.chikichiki.sako.tv.other.MainFragmentFactory

const val Library_HEADER_ID_1: Long = 1
const val Library_HEADER_NAME_1 = "History"
const val Library_HEADER_ID_2: Long = 2
const val Library_HEADER_NAME_2 = "Watch Later"
const val Library_HEADER_ID_3: Long = 3
const val Library_HEADER_NAME_3 = "Support"
class LibraryTvFragment:BrowseSupportFragment(),MainFragmentAdapterProvider {
    private lateinit var backgroundManager: BackgroundManager
    private lateinit var mAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUi()
        loadData()

        mainFragmentRegistry.registerFragment(PageRow::class.java,
            LibraryFragmentFactory(backgroundManager)
        )


    }

    private fun setupUi(){
        backgroundManager = BackgroundManager.getInstance(activity)


        this.headersState = BrowseSupportFragment.HEADERS_ENABLED

        this.isHeadersTransitionOnBackEnabled = false
        this.brandColor = ContextCompat.getColor(requireActivity(), R.color.dark_grey)

        prepareEntranceTransition()
    }

    private fun loadData(){
        mAdapter= ArrayObjectAdapter(ListRowPresenter())
        this.adapter = mAdapter

        Thread{
            Thread.sleep(1000)
            createRows()
            startEntranceTransition()
        }.start()

        progressBarManager.hide()
    }

    private fun createRows(){
        val headerItem1 = HeaderItem(Library_HEADER_ID_1,Library_HEADER_NAME_1)
        val pageRow1 = PageRow(headerItem1)
        mAdapter.add(pageRow1)

        val headerItem2 = HeaderItem(Library_HEADER_ID_2,Library_HEADER_NAME_2)
        val pageRow2 = PageRow(headerItem2)
        mAdapter.add(pageRow2)


        val headerItem3 = HeaderItem(Library_HEADER_ID_3,Library_HEADER_NAME_3)
        val pageRow3 = PageRow(headerItem3)
        mAdapter.add(pageRow3)



    }

    override fun getMainFragmentAdapter(): MainFragmentAdapter<*> {
        return MainFragmentAdapter(this)
    }


}