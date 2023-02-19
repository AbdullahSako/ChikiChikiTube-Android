package tube.chikichiki.sako

import tube.chikichiki.sako.model.*


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


    //get playlists of a channel based on channel id
    fun playlistsOfChannel(list: List<VideoPlaylist>, channelId: Int?): List<VideoPlaylist> {
        val filteredList: MutableList<VideoPlaylist> = mutableListOf()

        list.forEach {
            if (it.videoChannel.id == channelId) {
                filteredList.add(it)
            }
        }

        return filteredList
    }


    fun sortChannels(channels: List<VideoChannel>): List<VideoChannel> {

        val sortedChannels = arrayOf(
            "gakinotsukai",
            "gottsueekanji",
            "knightscoop",
            "suiyoubinodowntown",
            "documental",
            "lincoln",
            "downtownnow",
            "worlddowntown",
            "heyheyhey",
            "matsumotoke",
            "ashitagaarusa",
            "mhk",
            "suberanaihanashi",
            "visualbum",
            "hitoshimatsumotostore"
        )
        val temp: MutableList<Pair<Int, VideoChannel>> = mutableListOf()
        val leftOver = mutableListOf<VideoChannel>()

        //sort channels based on sorted channels array
        channels.forEach {
            val index = sortedChannels.indexOf(it.channelHandle)
            if (index != -1) {
                temp.add(index to it)
            } else {
                leftOver.add(it)
            }
        }

        temp.sortBy { it.first }
        leftOver.forEach { leftOverListItem -> temp.add(channels.size to leftOverListItem) }

        val sorted: MutableList<VideoChannel> = mutableListOf()

        temp.forEach { sorted.add(it.second) }


        //remove empty channels
        return sorted.filter { it.channelHandle != "root_channel" && it.channelHandle != "fearfulkyochan" && it.channelHandle != "chikichikitube" }

    }

}