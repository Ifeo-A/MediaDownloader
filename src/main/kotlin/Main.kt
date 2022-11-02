import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import components.*
import data.CommandOptions
import kotlinx.coroutines.launch
import theme.*
import util.Constants.AUDIO_FORMAT_OPTIONS
import util.Constants.AUDIO_MEDIA_FORMAT
import util.Constants.Buttons.START
import util.Constants.Buttons.STOP
import util.Constants.DOWNLOAD_COMPLETE
import util.Constants.FILE
import util.Constants.MAIN_WINDOW_TITLE
import util.Constants.MEDIA_FORMAT
import util.Constants.MEDIA_FORMAT_OPTIONS
import util.Constants.Settings.SAVE_LOCATION
import util.Constants.Settings.SETTINGS
import util.Constants.Settings.SettingsJSON.DEFAULT_DOWNLOAD_DIRECTORY
import util.Constants.VIDEO_FORMAT_OPTIONS
import util.Constants.VIDEO_MEDIA_FORMAT
import util.DownloadUtil.isDownloadComplete
import util.SettingsUtil


@Composable
@Preview
fun App(theDownloadLocation: String?) {

    val viewModel = ViewModel()
//    var downloadUrl by remember { mutableStateOf("https://www.youtube.com/watch?v=bhrumYeZvjs") }
    var downloadUrl by remember { mutableStateOf("https://www.youtube.com/watch?v=NRpNUi5e7Os") }
    var selectedMediaFormat by remember { mutableStateOf(VIDEO_MEDIA_FORMAT) } // Video or Audio
    var selectedMediaFileExtension by remember { mutableStateOf(VIDEO_FORMAT_OPTIONS.first()) } //"mp4
    var isFileChooserOpen by remember { mutableStateOf(false) }
    var saveLocationButtonClicked by remember { mutableStateOf(false) }
    var downloadPercentage by remember { mutableStateOf("0") }
    var downloadButtonText by remember { mutableStateOf(START) }
    var isDownloading by remember { mutableStateOf(false) }
    var mediaName: String by remember { mutableStateOf("") }
//    var downloadLocation: String? by remember { mutableStateOf(SettingsUtil.readDownloadLocation()) }
    var downloadLocation: String? = theDownloadLocation
    val myCoroutineScope = rememberCoroutineScope()

    MaterialTheme {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush
                        .linearGradient(listOf(red, purple))
                )
                .padding(horizontal = 32.dp, vertical = 24.dp)
        ) {

            UrlBar(downloadUrl) { urlBarText ->
                downloadUrl = urlBarText
            }

            Spacer(Modifier.padding(vertical = 30.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                DropDown(
                    dropDownTitle = MEDIA_FORMAT,
                    dropDownOptions = MEDIA_FORMAT_OPTIONS,
                    onOptionSelected = {
                        println(it)
                        selectedMediaFormat = it
                    },
                    selectedDefault = selectedMediaFormat
                )

                when (selectedMediaFormat) {
                    VIDEO_MEDIA_FORMAT -> {
                        selectedMediaFileExtension = VIDEO_FORMAT_OPTIONS.first()
                        DropDown(
                            dropDownTitle = VIDEO_MEDIA_FORMAT,
                            dropDownOptions = VIDEO_FORMAT_OPTIONS,
                            onOptionSelected = { fileExtension ->
                                println(fileExtension)
                                selectedMediaFileExtension = fileExtension.lowercase()
                            },
                            selectedDefault = VIDEO_FORMAT_OPTIONS.first()
                        )
                    }
                    AUDIO_MEDIA_FORMAT -> {
                        selectedMediaFileExtension = AUDIO_FORMAT_OPTIONS.first()
                        DropDown(
                            dropDownTitle = AUDIO_MEDIA_FORMAT,
                            dropDownOptions = AUDIO_FORMAT_OPTIONS,
                            onOptionSelected = { fileExtension ->
                                println(fileExtension)
                                selectedMediaFileExtension = fileExtension.lowercase()
                            },
                            selectedDefault = AUDIO_FORMAT_OPTIONS.first()
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
                // Open file chooser if save location button is clicked
                if (saveLocationButtonClicked) {
                    FilePicker(
                        onCloseRequest = { _filePath ->
                            isFileChooserOpen = false
                            saveLocationButtonClicked = false
                            downloadLocation = _filePath
                            println("Download location: $_filePath")
                        },
                        onError = { errorMessage ->
                            isFileChooserOpen = false
                            saveLocationButtonClicked = false
                            println("Error: $errorMessage")
                        }
                    )
                }

                // Save location button
                GlassButton(
                    buttonText = SAVE_LOCATION,
                    onButtonClick = {
                        //Open file picker
                        saveLocationButtonClicked = true
                    }
                )
                MyText(text = downloadLocation ?: "")

                Divider(Modifier.padding(vertical = 30.dp))

                //Start/STOP button - start download
                GlassButton(
                    buttonText = downloadButtonText,
                    onButtonClick = {
                        if (!isDownloading) {

                            val commandOptions = CommandOptions(
                                url = downloadUrl,
                                downloadFolder = downloadLocation ?: DEFAULT_DOWNLOAD_DIRECTORY,
                                format = selectedMediaFileExtension.lowercase()
                            )
                            println("CommandOptions Download location: ${commandOptions.downloadFolder}")
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
                )

                if (mediaName.isNotEmpty()) {
                    MyText(
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
                MyText(
                    text = if (isDownloadComplete(downloadPercentage)) {
                        DOWNLOAD_COMPLETE
                    } else "${downloadPercentage}%"
                )

            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    var shouldOpenSettingsWindow by remember { mutableStateOf(false) }
    var downloadLocation by remember { mutableStateOf(SettingsUtil.readDownloadLocationFromSettingsFile() ?: "") }

//    File(DEFAULT_DOWNLOAD_DIRECTORY).deleteRecursively()
    SettingsUtil.initDefaultDirectories()

    Window(
        title = MAIN_WINDOW_TITLE,
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(width = WINDOW_WIDTH, height = WINDOW_HEIGHT)
    ) {

        MenuBar {
            Menu(FILE, mnemonic = ',') {
                Item(
                    SETTINGS,
                    onClick = {
                        shouldOpenSettingsWindow = true
                    },
                    shortcut = KeyShortcut(Key.Settings, ctrl = true)
                )
            }
        }

        if (shouldOpenSettingsWindow) {
            SettingsWindow(
                windowClose = {
                    shouldOpenSettingsWindow = false
                },
                downloadChooserButtonClicked = {},
                downloadDirectoryChanged = { newDownloadDirectory ->
                    downloadLocation = newDownloadDirectory
                    println("Set new download directory to: $downloadLocation")
                }
            )
        }

        App(downloadLocation)
    }
}
