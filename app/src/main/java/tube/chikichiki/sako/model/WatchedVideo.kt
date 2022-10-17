package tube.chikichiki.sako.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class WatchedVideo(@PrimaryKey val uuid: UUID, var watchedVideoTimeInMil:Float)
