package tube.chikichiki.sako.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import tube.chikichiki.sako.model.HistoryVideoInfo
import tube.chikichiki.sako.model.WatchedVideo
import java.util.UUID
import java.util.concurrent.Executors

private const val DATABASE_NAME = "ChikiChiki-database"
class ChikiChikiDatabaseRepository private constructor(context: Context){
    private val database:ChikiChikiDatabase = Room.databaseBuilder(context.applicationContext,ChikiChikiDatabase::class.java,
        DATABASE_NAME).build()

    private val chikiChikiDao = database.chikiChikiDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun getHistory():LiveData<List<HistoryVideoInfo>> = chikiChikiDao.getHistory()

    fun getWatchedVideo(uuid: UUID):LiveData<WatchedVideo> = chikiChikiDao.getWatchedVideo(uuid)

    fun removeFromHistory(historyVideoInfo: HistoryVideoInfo) = chikiChikiDao.removeFromHistory(historyVideoInfo)

    fun addToHistory(historyVideoInfo: HistoryVideoInfo){
        executor.execute {
            chikiChikiDao.addToHistory(historyVideoInfo)
        }
    }

    fun addWatchedVideo(watchedVideo: WatchedVideo){
        executor.execute {
            chikiChikiDao.addWatchedVideo(watchedVideo)
        }
    }

    companion object{
        private var instance:ChikiChikiDatabaseRepository?=null

        fun initialize(context: Context){
            if(instance==null){
                instance= ChikiChikiDatabaseRepository(context)
            }
        }

        fun get():ChikiChikiDatabaseRepository{
            return instance?: throw IllegalStateException("ChikiChikiDatabaseRepository must be initialized")
        }

    }
}