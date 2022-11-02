package tube.chikichiki.sako.tv.fragment

import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import tube.chikichiki.sako.R
import tube.chikichiki.sako.tv.other.PageRowFragmentFactory
import java.lang.Thread.sleep

const val HEADER_ID_1: Long = 1
const val HEADER_NAME_1 = "Shows"

class PageAndListRowFragment:BrowseSupportFragment() {

     val HEADER_ID_2: Long = 2
     val HEADER_NAME_2 = "Rows Fragment"
     val HEADER_ID_3: Long = 3
     val HEADER_NAME_3 = "Settings Fragment"
     val HEADER_ID_4: Long = 4
     val HEADER_NAME_4 = "User agreement Fragment"
    private lateinit var backgroundManager:BackgroundManager

    private lateinit var mRowsAdapter:ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupUi()
        loadData()
        backgroundManager =BackgroundManager.getInstance(activity)
        backgroundManager.attach(activity?.window)
        mainFragmentRegistry.registerFragment(PageRow::class.java,PageRowFragmentFactory(backgroundManager))

    }

    private fun setupUi(){
        this.headersState = BrowseSupportFragment.HEADERS_ENABLED
        this.isHeadersTransitionOnBackEnabled = true
        this.brandColor = ContextCompat.getColor(requireActivity(), R.color.orange)
        this.title = "Chiki Chiki Tube"


        this.setOnSearchClickedListener {
            Toast.makeText(activity,"SEARCH",Toast.LENGTH_SHORT).show()
        }

        prepareEntranceTransition()
    }

    private fun loadData(){
        mRowsAdapter= ArrayObjectAdapter(ListRowPresenter())
        this.adapter = mRowsAdapter

        Thread{
            sleep(2000)
            createRows()
            startEntranceTransition()
        }.start()
    }

    private fun createRows(){
        val headerItem1 = HeaderItem(HEADER_ID_1,HEADER_NAME_1)
        val pageRow1 = PageRow(headerItem1)
        mRowsAdapter.add(pageRow1)

        /*val headerItem2 = HeaderItem(HEADER_ID_2,HEADER_NAME_2)
        val pageRow2 = PageRow(headerItem2)
        mRowsAdapter.add(pageRow2)

        val headerItem3 = HeaderItem(HEADER_ID_3,HEADER_NAME_3)
        val pageRow3 = PageRow(headerItem3)
        mRowsAdapter.add(pageRow3)

        val headerItem4 = HeaderItem(HEADER_ID_4,HEADER_NAME_4)
        val pageRow4 = PageRow(headerItem4)
        mRowsAdapter.add(pageRow4)*/

    }






















}
