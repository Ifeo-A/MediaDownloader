package data

data class CommandOptions(
    val downloadTool: String = "youtube-dl",
    val url: String,
    val downloadFolder: String,
    val format: String
){
    fun builtCommand(): List<String>{
        val array = mutableListOf<String>(downloadTool)

//        when(format){
//            MediaFormat.AudioMediaFormat.AAC,
//            MediaFormat.AudioMediaFormat.MP3 -> {
//                array.add("--extract-audio")
//                array.add("--audio-format")
//                array.add(format.toString())
//            }
//            MediaFormat.VideoMediaFormat.MP4 -> {
//                array.add("--format")
//                array.add(format.toString())
//            }
//        }

        when(format){
            "mp3",
            "aac" -> {
                array.add("--extract-audio")
                array.add("--audio-format")
                array.add(format)
            }
            "mp4" -> {
                array.add("--format")
                array.add(format)
            }
            else -> {}
        }
        array.add(url)
        array.add(downloadFolder)

        return array
    }
}