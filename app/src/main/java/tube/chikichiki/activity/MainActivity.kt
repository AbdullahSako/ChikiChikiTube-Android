package tube.chikichiki.activity

import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

import androidx.navigation.fragment.NavHostFragment

import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import tube.chikichiki.R
import tube.chikichiki.api.ChikiFetcher

import tube.chikichiki.model.VideoPlaylist

class MainActivity : AppCompatActivity() {

    private lateinit var toolbarGrainAnimation: AnimationDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar:Toolbar= findViewById(R.id.toolbar)
        val bottomNavBar:BottomNavigationView=findViewById(R.id.bottomNavigationView)


        //set animation of toolbar and navbar
        toolbar.apply {
            setBackgroundResource(R.drawable.grain_animation)
            toolbarGrainAnimation= background as AnimationDrawable
        }
        toolbarGrainAnimation.start()

        //get navigation controller
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController= navHost.navController

        //set up bottom nav bar to work with navigation graph
        NavigationUI.setupWithNavController(bottomNavBar,navController)

        //set a toolbar to activity
        setSupportActionBar(toolbar)

        //remove app name from toolbar
        supportActionBar?.setDisplayShowTitleEnabled(false)



    }
}