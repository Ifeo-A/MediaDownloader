package util

import data.DownloadProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.regex.Matcher
import java.util.regex.Pattern

object DownloadUtil {

    suspend fun startDownload(
        downloadFolder: String,
        builtCommand: List<String>
    ): Flow<DownloadProperties> = channelFlow {
        val youtubeDlCommand = ProcessBuilder(builtCommand)
        youtubeDlCommand.directory(File(downloadFolder))

        coroutineScope {
            launch(Dispatchers.IO) {
//            val pwdCommand = ProcessBuilder("pwd")
//            pwdCommand.directory(File(downloadFolder))
//            pwdCommand.redirectOutput(File("${downloadFolder}/output.log"))
//            val pwdCommandProcess = pwdCommand.start()

                val youtubeDlCommandProcess = youtubeDlCommand.start()

                CommandUtil.getOutputFromCommand(youtubeDlCommandProcess).collect { outputFromCommand ->
                    if (outputFromCommand.contains("[download]")) {
                        send(
                            DownloadProperties(
                                mediaTitle = findMediaName(outputFromCommand),
                                downloadPercentageCompleted = findDownloadProgress(outputFromCommand),
                                process = youtubeDlCommandProcess
                            )
                        )
                    }
                    if (outputFromCommand.contains("[download] 100%")) {
                        // Download complete
                    }
                }
            }
        }
    }

    fun stopDownload(process: Process) {
        process.destroy()
        println("Download stopped")
    }

    fun deleteUnfinishedDownloadedFile(file: File) {
        file.delete()
    }

    fun isDownloadComplete(downloadPercentage: String): Boolean = downloadPercentage == "100"

    /**
     * Returns string value representing the download progress
     *
     * @param String - String to search for download percentage e.g "[download] 34.9% ..."
     * @return String - containing download percentage value e.g "39" without the percentage sign
     */
    private fun findDownloadProgress(str: String): String {

        // Given the string "[download]   0.3% of 2.56MiB at 63.91KiB/s ETA 00:40"
        // This will match "0.3%"
        val regexPatternPercentage = "\\b(?<!\\.)(?!0+(?:\\.0+)?%)(?:\\d|[1-9]\\d|100)(?:(?<!100)\\.\\d+)?%"
        val regexPatternPercentagePattern: Pattern = Pattern.compile(regexPatternPercentage)
        val regexPatternPercentagePatternMatcher: Matcher = regexPatternPercentagePattern.matcher(str)

        val percentValue = if (regexPatternPercentagePatternMatcher.find()) {
            regexPatternPercentagePatternMatcher.group(0)
        } else {
            // No match
            "0"
        }

        return percentValue.replace("%", "")
    }

    private fun findMediaName(str: String): String {
        // Pattern to find the line that contains the string [download]
        // Given the string "[download] Destination: MEMES OF THE DAY - OFF WITH THEIR MEMES-bhrumYeZvjs.mp4"
        // This will match "MEMES OF THE DAY - OFF WITH THEIR MEMES-bhrumYeZvjs.mp4"
        val regexPatternMediaName = "(?<=] Destination: )(.*)"
        val mediaNamePattern: Pattern = Pattern.compile(regexPatternMediaName)
        val mediaNameMatcher: Matcher = mediaNamePattern.matcher(str)

        val mediaTitle: String =
            if (mediaNameMatcher.find()) {
                mediaNameMatcher.group(0)
            } else {
                // No match
                ""
            }

        return mediaTitle
    }

}