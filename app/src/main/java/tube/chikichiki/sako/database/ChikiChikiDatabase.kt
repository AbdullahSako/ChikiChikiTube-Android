package tube.chikichiki.sako.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import tube.chikichiki.sako.model.HistoryVideoInfo
import tube.chikichiki.sako.model.Video
import tube.chikichiki.sako.model.WatchedVideo

@Database(entities = [HistoryVideoInfo::class,WatchedVideo::class], version = 3)
@TypeConverters(ChikiChikiTypeConverters::class)
abstract class ChikiChikiDatabase: RoomDatabase() {

    abstract fun chikiChikiDao():ChikiChikiDao

}