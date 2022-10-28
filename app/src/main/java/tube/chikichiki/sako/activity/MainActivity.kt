package tube.chikichiki.sako.activity


import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import tube.chikichiki.sako.Utils
import tube.chikichiki.sako.R
import tube.chikichiki.sako.fragment.*
import kotlin.random.Random

private const val SEARCH_FRAGMENT_TAG:String="SEARCHFRAGMENT"
class MainActivity : AppCompatActivity() {

    private lateinit var toolbarGrainAnimation: AnimationDrawable
    override fun onCreate(savedInstanceState: Bundle?) {
        //handle splash screen transition
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar:Toolbar= findViewById(R.id.toolbar)
        val bottomNavBar:BottomNavigationView=findViewById(R.id.bottomNavigationView)
        if(savedInstanceState ==null){
            setFragment(MainFragment())
        }



        //change fragments on bottom nav bar item selected
        bottomNavBar.setOnItemSelectedListener {

            when(it.itemId){

                R.id.mainFragment-> setFragment(MainFragment())
                R.id.mostViewedVideosFragment-> setFragment(MostViewedVideosFragment())
                R.id.recentVideosFragment->setFragment(RecentVideosFragment())
                R.id.libraryFragment->setFragment(LibraryFragment.newInstance())

            }
            true
        }



        //set animation of toolbar and navbar
        toolbar.apply {
            setBackgroundResource(R.drawable.grain_animation)
            toolbarGrainAnimation= background as AnimationDrawable
        }
        toolbarGrainAnimation.start()




        //set a toolbar to activity
        setSupportActionBar(toolbar)

        //remove app name from toolbar
        supportActionBar?.setDisplayShowTitleEnabled(false)

        //set search functionality
        setSearchFunctionality()



    }

    private fun setSearchFunctionality(){
        val searchBtn:ImageButton=findViewById(R.id.main_activity_search_button)
        val searchEditText:EditText=findViewById(R.id.main_activity_search_edit_text)
        val searchBackBtn:ImageButton=findViewById(R.id.main_activity_back_image_button)
        val toolbarLogo:ImageView=findViewById(R.id.toolbar_image)
        val toolbarText:TextView=findViewById(R.id.toolbar_text)
        val toolbarHeaderText:TextView=findViewById(R.id.header_text)


        searchBtn.setOnClickListener {
            //hide toolbar
            toolbarLogo.visibility=View.INVISIBLE
            toolbarText.visibility=View.INVISIBLE
            toolbarHeaderText.visibility=View.INVISIBLE
            searchBtn.visibility=View.INVISIBLE

            //show search views
            searchEditText.visibility=View.VISIBLE
            searchBackBtn.visibility=View.VISIBLE

            //focus on search edit text and show keyboard
            searchEditText.requestFocus()
            val input: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            input.showSoftInput(searchEditText,0)

        }

        searchBackBtn.setOnClickListener {
            //show toolbar
            toolbarLogo.visibility=View.VISIBLE
            toolbarText.visibility=View.VISIBLE
            toolbarHeaderText.visibility=View.VISIBLE
            searchBtn.visibility=View.VISIBLE

            //hide search views
            searchEditText.visibility=View.GONE
            searchBackBtn.visibility=View.GONE


            //clear search edit text
            searchEditText.setText("")

            //remove search fragment
            val fragment=supportFragmentManager.findFragmentByTag(SEARCH_FRAGMENT_TAG)
            if(fragment!=null){
                supportFragmentManager.beginTransaction().remove(fragment).commit()
            }


            //hide keyboard
            val input:InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            input.hideSoftInputFromWindow(searchEditText.windowToken,0)

        }
        // search button in keyboard
        searchEditText.setOnEditorActionListener { _, i, _ ->
            if(searchEditText.text.isNotEmpty()){
                if(i==EditorInfo.IME_ACTION_SEARCH){


                    //remove previous search fragment if it exists
                    val fragment=supportFragmentManager.findFragmentByTag(SEARCH_FRAGMENT_TAG)
                    if(fragment!=null){
                        supportFragmentManager.beginTransaction().remove(fragment).commit()
                    }


                    supportFragmentManager.beginTransaction().apply {
                        add(R.id.main_fragment_container,MainSearchFragment.newInstance(searchEditText.text.toString()),
                            SEARCH_FRAGMENT_TAG)
                        commit()
                    }

                }
            }

            //hide keyboard
            val input:InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            input.hideSoftInputFromWindow(searchEditText.windowToken,0)
        }

    }

    private fun setFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.main_fragment_container,fragment)
            commit()
        }
    }

    override fun onResume() {
        super.onResume()
        //bring back to start in case user clicked the home button while motion layout was at end
        //findViewById<MotionLayout>(R.id.activity_main_motion_layout).transitionToStart()
    }

}