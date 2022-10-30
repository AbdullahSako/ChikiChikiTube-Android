package tube.chikichiki.sako

import tube.chikichiki.sako.model.Video
import tube.chikichiki.sako.model.VideoAndWatchedTimeModel
import tube.chikichiki.sako.model.WatchedVideo


object Utils {
    var IsInPipMode = false


    //creates a pair of videos with their corresponding watch time
    fun getPairOfVideos(
        videos: List<Video>,
        watchedTime: List<WatchedVideo>
    ): List<VideoAndWatchedTimeModel> {
        val videoAndWatchedTime: MutableList<VideoAndWatchedTimeModel> = mutableListOf()


        videos.forEachIndexed { index, video ->
            videoAndWatchedTime.add(VideoAndWatchedTimeModel(video))

            watchedTime.forEach {
                if (video.uuid == it.uuid) {
                    videoAndWatchedTime[index] = videoAndWatchedTime[index].copy(
                        video,
                        (((it.watchedVideoTimeInMil / 1000) / video.duration.toFloat()) * 100).toLong()
                    )
                }
            }

        }
        return videoAndWatchedTime

    }

}