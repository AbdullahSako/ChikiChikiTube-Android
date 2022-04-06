package tube.chikichiki.sako.model

data class Banner(var path: String = "") {

    fun getFullPath(): String {
        return "https://vtr.chikichiki.tube$path"
    }
}