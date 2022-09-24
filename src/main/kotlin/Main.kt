import Constants.AUDIO_FORMAT
import Constants.AUDIO_OPTIONS
import Constants.Buttons.START
import Constants.Buttons.STOP
import Constants.INVALID_FILE_PATH
import Constants.SAVE_LOCATION
import Constants.URL_PLACEHOLDER
import Constants.VIDEO_FORMAT
import Constants.VIDEO_OPTIONS
import Util.CommandUtil
import Util.DownloadUtil
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
    val downloadUtil = DownloadUtil()
    val commandUtil = CommandUtil()

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
                    dropDownOptions = AUDIO_OPTIONS,
                    onOptionSelected = {
                        println(it)
                    }
                )
                DropDown(
                    dropDownTitle = VIDEO_FORMAT,
                    dropDownOptions = VIDEO_OPTIONS,
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

                // Save location button
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

                //Start/STOP button - start download
                Button(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    onClick = {
                        downloadButtonText = if (downloadButtonText == START) STOP else START
                        downloadPercentage = "0"

                        if (!isDownloading) {
                            isDownloading = true

                            // TODO logic to check if the filename that the download would produce
                            //  already exists and if it does then not to start download
                            myCoroutineScope.launch(Dispatchers.IO) {
                                launch {
                                    downloadUtil.startDownload(commandUtil).collect { (process ,_downloadPercentage) ->
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

                Text(text = if (downloadPercentage == "100") "DOWNLOAD COMPLETE" else "${downloadPercentage}%")

            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
