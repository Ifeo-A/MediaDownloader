import Constants.AUDIO_FORMAT
import Constants.Buttons.START
import Constants.Buttons.STOP
import Constants.INVALID_FILE_PATH
import Constants.SAVE_LOCATION
import Constants.URL_PLACEHOLDER
import Constants.VIDEO_FORMAT
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import components.DropDown
import components.FilePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.util.regex.Matcher
import java.util.regex.Pattern

@Composable
@Preview
fun App() {
    var url by remember { mutableStateOf("") }
    var isFileChooserOpen by remember { mutableStateOf(false) }
    var fileChooserButtonClicked by remember { mutableStateOf(false) }
    var downloadPercentage by remember { mutableStateOf("0") }
    var downloadButtonText by remember { mutableStateOf("START") }
    var isDownloading by remember { mutableStateOf(false) }
    var filePath: String? by remember { mutableStateOf("") }
    val myCoroutineScope = rememberCoroutineScope()

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // URL bar
                TextField(
                    modifier = Modifier.weight(1f),
                    value = url,
                    onValueChange = { url = it },
                    placeholder = {
                        Text(URL_PLACEHOLDER)
                    }
                )
            }

            Spacer(Modifier.padding(vertical = 30.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                DropDown(
                    dropDownTitle = AUDIO_FORMAT,
                    dropDownOptions = listOf("MP3"),
                    onOptionSelected = {
                        println(it)
                    }
                )
                DropDown(
                    dropDownTitle = VIDEO_FORMAT,
                    dropDownOptions = listOf("MP4"),
                    onOptionSelected = {
                        println(it)
                    }
                )
            }
            Spacer(Modifier.padding(vertical = 30.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (fileChooserButtonClicked) {
                    FilePicker(
                        onCloseRequest = { _filePath ->
                            isFileChooserOpen = false
                            fileChooserButtonClicked = false
                            filePath = _filePath
                            println("File: $_filePath")
                        },
                        onError = { errorMessage ->
                            isFileChooserOpen = false
                            fileChooserButtonClicked = false
                            println("Error: $errorMessage")
                        }
                    )
                }

                // Save location
                Button(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    onClick = {
                        //Open file picker
                        fileChooserButtonClicked = true
                    }
                ) {
                    Text(SAVE_LOCATION)
                }
                Text(text = filePath ?: INVALID_FILE_PATH)

                Divider(Modifier.padding(vertical = 30.dp))

                //Start button - start download
                Button(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    onClick = {
                        downloadButtonText = if (downloadButtonText == START) STOP else START
                        if (!isDownloading) {
                            isDownloading = true

                            // TODO logic to check if the filename that the download would produce
                            //  already exists and if it does then not to start download
                            myCoroutineScope.launch(Dispatchers.IO) {
                                launch {
                                    startDownload().collect { _downloadPercentage ->
                                        if (_downloadPercentage == "100") {
                                            isDownloading = false
                                            downloadButtonText = START
                                        }
                                        downloadPercentage = _downloadPercentage
                                        println("Download Percentage: ${downloadPercentage}%")
                                    }
                                }
                            }
                        }
                    }
                ) {
                    Text(text = downloadButtonText)
                }

                DownloadProgressIndicator(
                    percentValue = downloadPercentage,
                    onDownloadError = { errorMessage ->
                        println(errorMessage)
                    },
                    onDownloadComplete = {
                    }
                )

            }
        }
    }
}

suspend fun startDownload(): Flow<String> = channelFlow {
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

            getOutputFromCommand(youtubeDlCommandProcess).collect { outputFromCommand ->
                if (outputFromCommand.contains("[download]")) {
                    send(findDownloadProgress(outputFromCommand))
                }
                if (outputFromCommand.contains("[download] 100%")) {
                    println("DOWNLOAD COMPLETE")
                }
            }
        }

    }
}

fun getOutputFromCommand(process: Process): Flow<String> = flow {
    println("process output: ${process.outputStream}")

    val stdInput = BufferedReader(InputStreamReader(process.inputStream))
    var outputFromCommand: String

    try {
        while (true) {
            outputFromCommand = stdInput.readLine() ?: break
            emit(outputFromCommand)
        }
        process.destroy()
    } catch (e: IOException) {
        println("ERROR OCCURRED: ${e.printStackTrace()}")
    }
}
    .flowOn(Dispatchers.IO)

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

@Composable
@Preview
fun DownloadProgressIndicator(
    modifier: Modifier = Modifier,
    percentValue: String = "0",
    onDownloadComplete: () -> Unit,
    onDownloadError: (errorMessage: String) -> Unit,
) {
    Text(
        if (percentValue == "100") {
            "DOWNLOAD COMPLETE"
        } else {
            "${percentValue}%"
        }
    )

}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
