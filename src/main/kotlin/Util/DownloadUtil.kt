package Util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.regex.Matcher
import java.util.regex.Pattern

class DownloadUtil {

    suspend fun startDownload(commandUtil: CommandUtil): Flow<Pair<Process, String>> = channelFlow {
        val downloadFolder = "/Users/ife/Documents/mediaDownloader"
        coroutineScope {
            launch(Dispatchers.IO) {
                //youtube-dl --format "mp4" "https://www.youtube.com/watch?v=bhrumYeZvjs"
//            val pwdCommand = ProcessBuilder("pwd")
//            pwdCommand.directory(File(downloadFolder))
//            pwdCommand.redirectOutput(File("${downloadFolder}/output.log"))
//            val pwdCommandProcess = pwdCommand.start()


                val youtubeDlCommand =
                    ProcessBuilder("youtube-dl", "--format", "mp4", "https://www.youtube.com/watch?v=bhrumYeZvjs")
                youtubeDlCommand.directory(File(downloadFolder))
                val youtubeDlCommandProcess = youtubeDlCommand.start()

                commandUtil.getOutputFromCommand(youtubeDlCommandProcess).collect { outputFromCommand ->
                    if (outputFromCommand.contains("[download]")) {
                        send(Pair(youtubeDlCommandProcess, findDownloadProgress(outputFromCommand)))
                    }
                    if (outputFromCommand.contains("[download] 100%")) {
                        println("DOWNLOAD COMPLETE")
                    }
                }
            }
        }
    }

    fun stopDownload(process: Process) {
        println("Download stopped")
        process.destroy()
    }

    /**
     * Returns string value representing the download progress
     *
     * @param String String to search for download percentage e.g "[download] 34.9% ..."
     * @return String - containing download percentage value e.g "39" without the percentage sign
     */
    private fun findDownloadProgress(str: String): String {

        var percentValue = "0"

        // Pattern to find the line that contains the string [download]
        val regexPattern1 = "^(\\[download])\\s+(\\d+(\\.)?\\d%)"
        val pattern1: Pattern = Pattern.compile(regexPattern1)
        val matcher1: Matcher = pattern1.matcher(str)

        val extractedStringFromPass1: String =
            if (matcher1.find()) {
                matcher1.group(0)
            } else {
                // No match
                percentValue
            }

        println("extractedStringFromPass1: $extractedStringFromPass1")

        val regexPattern2 = "\\b(?<!\\.)(?!0+(?:\\.0+)?%)(?:\\d|[1-9]\\d|100)(?:(?<!100)\\.\\d+)?%"
        val pattern2: Pattern = Pattern.compile(regexPattern2)
        val matcher2: Matcher = pattern2.matcher(extractedStringFromPass1)

        if (matcher2.find()) {
            percentValue = matcher2.group(0)
        } else {
            // No match
        }

        return percentValue.replace("%", "")
    }

}