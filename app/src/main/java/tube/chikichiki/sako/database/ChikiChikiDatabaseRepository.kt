package tube.chikichiki.sako.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import tube.chikichiki.sako.model.HistoryVideoInfo
import tube.chikichiki.sako.model.WatchLater
import tube.chikichiki.sako.model.WatchedVideo
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "ChikiChiki-database"

class ChikiChikiDatabaseRepository private constructor(context: Context) {
    private val database: ChikiChikiDatabase = Room.databaseBuilder(
        context.applicationContext, ChikiChikiDatabase::class.java,
        DATABASE_NAME
    ).fallbackToDestructiveMigration().build()

    private val chikiChikiDao = database.chikiChikiDao()
    private val executor = Executors.newSingleThreadExecutor()

    //History

    //get only 20 items
    fun getHistorySmall(): LiveData<List<HistoryVideoInfo>> = chikiChikiDao.getHistorySmall()

    //get 200 items
    fun getHistoryBig(): LiveData<List<HistoryVideoInfo>> = chikiChikiDao.getHistoryBig()


    fun removeFromHistory(historyVideoInfo: HistoryVideoInfo) {
        executor.execute {
            chikiChikiDao.removeFromHistory(historyVideoInfo)
        }
    }

    fun addToHistory(historyVideoInfo: HistoryVideoInfo) {
        executor.execute {
            chikiChikiDao.addToHistory(historyVideoInfo)
        }
    }

    //watched video (saves the time the user watched for in order to show on video thumbnails)

    fun addWatchedVideo(watchedVideo: WatchedVideo) {
        executor.execute {
            chikiChikiDao.addWatchedVideo(watchedVideo)
        }
    }

    fun getAllWatchedVideos(): LiveData<List<WatchedVideo>> = chikiChikiDao.getAllWatchedVideos()

    fun getWatchedVideo(uuid: UUID): LiveData<WatchedVideo> = chikiChikiDao.getWatchedVideo(uuid)

    fun removeWatchedVideo(watchedVideo: WatchedVideo) {
        executor.execute {
            chikiChikiDao.removeWatchedVideo(watchedVideo)
        }
    }

    //watch later

    fun getAllWatchLater(): LiveData<List<WatchLater>> = chikiChikiDao.getAllWatchLater()

    fun getWatchLaterItem(uuid: UUID): LiveData<WatchLater> = chikiChikiDao.getWatchLater(uuid)

    fun addToWatchLater(watchLater: WatchLater) {
        executor.execute {
            chikiChikiDao.addToWatchLater(watchLater)
        }
    }

    fun removeFromWatchLater(watchLater: WatchLater) {
        executor.execute {
            chikiChikiDao.removeFromWatchLater(watchLater)
        }
    }


    companion object {
        private var instance: ChikiChikiDatabaseRepository? = null

        fun initialize(context: Context) {
            if (instance == null) {
                instance = ChikiChikiDatabaseRepository(context)
            }
        }

        fun get(): ChikiChikiDatabaseRepository {
            return instance
                ?: throw IllegalStateException("ChikiChikiDatabaseRepository must be initialized")
        }

    }
}