package tube.chikichiki.sako

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import tube.chikichiki.sako.database.ChikiChikiDatabaseRepository

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //night theme always on
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        //initialize room database repository
        ChikiChikiDatabaseRepository.initialize(this)
    }
}