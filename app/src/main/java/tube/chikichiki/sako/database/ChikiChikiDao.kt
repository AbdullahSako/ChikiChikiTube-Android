package tube.chikichiki.sako.database

import androidx.lifecycle.LiveData
import androidx.room.*
import tube.chikichiki.sako.model.HistoryVideoInfo
import tube.chikichiki.sako.model.WatchLater
import tube.chikichiki.sako.model.WatchedVideo
import java.util.*

@Dao
interface ChikiChikiDao {

    //History

    @Query("SELECT * FROM HistoryVideoInfo order by dateAdded desc LIMIT 20")
    fun getHistorySmall(): LiveData<List<HistoryVideoInfo>>

    @Query("SELECT * FROM HistoryVideoInfo order by dateAdded desc LIMIT 200")
    fun getHistoryBig(): LiveData<List<HistoryVideoInfo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addToHistory(historyVideoInfo: HistoryVideoInfo)

    @Delete
    fun removeFromHistory(historyVideoInfo: HistoryVideoInfo)

    //WATCHED VIDEO

    @Query("SELECT * FROM WatchedVideo where uuid=(:uuid)")
    fun getWatchedVideo(uuid: UUID): LiveData<WatchedVideo>

    @Query("SELECT * FROM WatchedVideo")
    fun getAllWatchedVideos(): LiveData<List<WatchedVideo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addWatchedVideo(watchedVideo: WatchedVideo)

    @Delete
    fun removeWatchedVideo(watchedVideo: WatchedVideo)

    //Watch Later

    @Query("SELECT * FROM WatchLater order by dateAdded desc")
    fun getAllWatchLater(): LiveData<List<WatchLater>>

    @Query("SELECT * FROM WatchLater where uuid = (:uuid)")
    fun getWatchLater(uuid: UUID): LiveData<WatchLater>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addToWatchLater(watchLater: WatchLater)

    @Delete
    fun removeFromWatchLater(watchLater: WatchLater)


}