package tube.chikichiki.sako.model

data class File(val torrentUrl:String, val torrentDownloadUrl:String, val fileUrl:String, val fileDownloadUrl:String, val metadataUrl:String, val resolution:Resolution) {
}