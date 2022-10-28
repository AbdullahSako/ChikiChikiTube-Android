package tube.chikichiki.sako.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

//This is used for progress bar under video thumbnails to show how much the user previously watched of the video
@Entity
data class WatchedVideo(@PrimaryKey val uuid: UUID, var watchedVideoTimeInMil:Long)
