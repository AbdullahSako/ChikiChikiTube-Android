package tube.chikichiki.sako.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import tube.chikichiki.sako.model.HistoryVideoInfo
import tube.chikichiki.sako.model.Video
import tube.chikichiki.sako.model.WatchedVideo
import java.util.UUID

@Dao
interface ChikiChikiDao {

    //History

    @Query("SELECT * FROM HistoryVideoInfo")
    fun getHistory():LiveData<List<HistoryVideoInfo>>

    @Insert
    fun addToHistory(historyVideoInfo: HistoryVideoInfo)

    @Delete
    fun removeFromHistory(historyVideoInfo: HistoryVideoInfo)

    //WATCHED VIDEO

    @Query("SELECT * FROM WatchedVideo where uuid=(:uuid)")
    fun getWatchedVideo(uuid:UUID):LiveData<WatchedVideo>

    @Insert
    fun addWatchedVideo(watchedVideo: WatchedVideo)


}