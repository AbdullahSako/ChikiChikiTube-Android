package tube.chikichiki.sako

import android.util.Log
import tube.chikichiki.sako.model.Video
import tube.chikichiki.sako.model.WatchedVideo


object Utils {
     var IsInPipMode=false


     //creates a pair of videos with their corresponding watch time
     fun getPairOfVideos(videos:List<Video>,watchedTime:List<WatchedVideo>) : List<Pair<Video,Long>>{
          val pairedVideos:MutableList<Pair<Video,Long>> = mutableListOf()

          //TODO FIX RECYCLERVIEW GOING ?BACK TO TOP
          //TODO FIX LOAD MORE
          videos.forEachIndexed { index, video ->
               pairedVideos.add(Pair(video,0))

               watchedTime.forEach {
                    if(video.uuid == it.uuid){
                         pairedVideos[index] = pairedVideos[index].copy(video,(((it.watchedVideoTimeInMil / 1000) / video.duration.toFloat())*100).toLong())
                    }
               }

          }
          return pairedVideos

     }

}