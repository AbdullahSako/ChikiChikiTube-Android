package tube.chikichiki.sako.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class HistoryVideoInfo(@PrimaryKey var uuid:UUID, var name:String, var previewPath:String)
