package tube.chikichiki.sako.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity
data class HistoryVideoInfo(@PrimaryKey var uuid:UUID, var name:String,var description:String, var previewPath:String ,var duration: Int,var dateAdded:Date)
{

    fun getFullThumbnailPath(): String {
        return "https://vtr.chikichiki.tube$previewPath"
    }

    fun getFormattedDuration(): String {
        return if (duration < 3600) {
            val seconds = duration % 60
            val minutes = (duration % 3600) / 60

            String.format("%02d:%02d", minutes, seconds)
        } else {
            val seconds = duration % 60
            val minutes = (duration % 3600) / 60
            val hours = duration / 3600
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }

    }
}
