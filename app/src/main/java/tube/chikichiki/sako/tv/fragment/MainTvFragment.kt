package tube.chikichiki.sako.tv.fragment

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import kotlinx.coroutines.*
import tube.chikichiki.sako.R
import tube.chikichiki.sako.tv.other.MainFragmentFactory

const val HEADER_ID_1: Long = 1
const val HEADER_NAME_1 = "Shows"
const val HEADER_ID_2: Long = 2
const val HEADER_NAME_2 = "Most Viewed"
const val HEADER_ID_3: Long = 3
const val HEADER_NAME_3 = "Recent"
const val HEADER_ID_4: Long = 4
const val HEADER_NAME_4 = "History"
const val HEADER_ID_5: Long = 5
const val HEADER_NAME_5 = "Watch Later"
const val HEADER_ID_6: Long = 6
const val HEADER_NAME_6 = "Support"

class MainTvFragment:BrowseSupportFragment() {


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

        setTitleFontAndColor()
        setSearchOrbColor()

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

        showSearchOrbAndTitle()




    }

    private fun loadData(){
        mRowsAdapter= ArrayObjectAdapter(ListRowPresenter())
        this.adapter = mRowsAdapter


        createRows()

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

        val headerItem5 = HeaderItem(HEADER_ID_5,HEADER_NAME_5)
        val pageRow5 = PageRow(headerItem5)
        mRowsAdapter.add(pageRow5)

        val headerItem6= HeaderItem(HEADER_ID_6,HEADER_NAME_6)
        val pageRow6 = PageRow(headerItem6)
        mRowsAdapter.add(pageRow6)


    }


    fun showSearchOrbAndTitle(){
        setOnSearchClickedListener {

        }
        this.title = resources.getString(R.string.chikichikitube)

        if(this.titleView !=null){
            this.titleView.alpha = 0f
            this.titleView.animate().alpha(1f).duration = 200
        }



    }

    fun hideSearchOrbAndTitle(){
        setOnSearchClickedListener(null)

        this.titleView.alpha = 1f
        this.titleView.animate().alpha(0f).duration = 200

    }

    private fun setTitleFontAndColor(){
        val textView=view?.findViewById<TextView>(androidx.leanback.R.id.title_text)
        textView?.setTextColor(ContextCompat.getColor(requireActivity(),R.color.font_pink))
        textView?.typeface = ResourcesCompat.getFont(requireActivity(),R.font.mochiypoppone)

    }

    private fun setSearchOrbColor(){
        view?.findViewById<SearchOrbView>(androidx.leanback.R.id.title_orb)?.orbColor = ContextCompat.getColor(requireActivity(),R.color.orange)
    }


}