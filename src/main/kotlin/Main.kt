import Constants.AUDIO_MEDIA_FORMAT
import Constants.AUDIO_OPTIONS
import Constants.Buttons.START
import Constants.Buttons.STOP
import Constants.DOWNLOAD_COMPLETE
import Constants.MEDIA_FORMAT_OPTIONS
import Constants.INVALID_FILE_PATH
import Constants.SAVE_LOCATION
import Constants.URL_PLACEHOLDER
import Constants.VIDEO_MEDIA_FORMAT
import Constants.VIDEO_OPTIONS
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
import kotlinx.coroutines.launch


@Composable
@Preview
fun App() {
    val viewModel = ViewModel()
//    var downloadUrl by remember { mutableStateOf("https://www.youtube.com/watch?v=bhrumYeZvjs") }
    var downloadUrl by remember { mutableStateOf("https://www.youtube.com/watch?v=NRpNUi5e7Os") }
    var selectedMediaFormat by remember { mutableStateOf(VIDEO_MEDIA_FORMAT) }
    var isFileChooserOpen by remember { mutableStateOf(false) }
    var fileChooserButtonClicked by remember { mutableStateOf(false) }
    var downloadPercentage by remember { mutableStateOf("0") }
    var downloadButtonText by remember { mutableStateOf("START") }
    var isDownloading by remember { mutableStateOf(false) }
    var mediaName: String by remember { mutableStateOf("") }
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
                    value = downloadUrl,
                    singleLine = true,
                    onValueChange = { downloadUrl = it },
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
                    dropDownTitle = "Media Format",
                    dropDownOptions = MEDIA_FORMAT_OPTIONS,
                    onOptionSelected = {
                        println(it)
                        selectedMediaFormat = it
                    },
                    selectedDefault = selectedMediaFormat
                )

                when (selectedMediaFormat) {
                    VIDEO_MEDIA_FORMAT -> {
                        DropDown(
                            dropDownTitle = VIDEO_MEDIA_FORMAT,
                            dropDownOptions = VIDEO_OPTIONS,
                            onOptionSelected = {
                                println(it)
                            }
                        )
                    }
                    AUDIO_MEDIA_FORMAT -> {
                        DropDown(
                            dropDownTitle = AUDIO_MEDIA_FORMAT,
                            dropDownOptions = AUDIO_OPTIONS,
                            onOptionSelected = {
                                println(it)
                            }
                        )
                    }
                }
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
                        if (!isDownloading) {
                            val commandOptions = CommandOptions(
                                url = downloadUrl,
                                format = "mp4"
                            )
                            viewModel.startDownload(commandOptions)
                            downloadPercentage = "0"
                            isDownloading = true

                            // TODO logic to check if the filename that the download would produce
                            //  already exists and if it does then not to start download

                            myCoroutineScope.launch {
                                viewModel.downloadProperties.collect { downloadProperties ->
                                    if (isDownloadComplete(downloadProperties.downloadPercentageCompleted)) {
                                        //Download is complete
                                        isDownloading = false
                                    }

                                    if (downloadProperties.mediaTitle.isNotEmpty()) {
                                        mediaName = downloadProperties.mediaTitle
                                    }

                                    downloadButtonText =
                                        if (isDownloadComplete(downloadProperties.downloadPercentageCompleted)) {
                                            START
                                        } else {
                                            STOP
                                        }
                                    downloadPercentage = downloadProperties.downloadPercentageCompleted
                                    println("Download Percentage: ${downloadPercentage}%")
                                }
                            }
                        }

                        // isDownloading will be true at this point if the user already pressed the START button
                        else {
                            viewModel.stopDownload()

                            downloadButtonText = START
                            downloadPercentage = "0"
                            mediaName = ""
                            isDownloading = false
                        }
                    }
                ) {
                    Text(text = downloadButtonText)
                }

                if (mediaName.isNotEmpty()) {
                    Text(
                        text = if (isDownloadComplete(downloadPercentage)) {
                            "Downloaded: $mediaName"
                        } else {
                            "Downloading: $mediaName"
                        }
                    )
                }
                if (isDownloadComplete(downloadPercentage)) {
                    downloadButtonText = START
                }
                Text(
                    text = if (isDownloadComplete(downloadPercentage)) {
                        DOWNLOAD_COMPLETE
                    } else "${downloadPercentage}%"
                )

            }
        }
    }
}

fun isDownloadComplete(downloadPercentage: String): Boolean = downloadPercentage == "100"

fun main() = application {
    Window(
        title = "Media Downloader",
        onCloseRequest = ::exitApplication
    ) {
        App()
    }
}
