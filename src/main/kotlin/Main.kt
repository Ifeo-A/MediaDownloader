// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
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
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.TimeUnit

@Composable
@Preview
fun App() {
    var url by remember { mutableStateOf("") }
    var isFileChooserOpen by remember { mutableStateOf(false) }
    var fileChooserButtonClicked by remember { mutableStateOf(false) }
    var filePath: String? by remember { mutableStateOf("") }
    val myCoroutineScope = rememberCoroutineScope()

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 24.dp)
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ){
                TextField(
                    modifier = Modifier.weight(1f),
                    value = url,
                    onValueChange = { url = it},
                    placeholder = {
                        Text("Paste URL here")
                    }
                )
            }

            Spacer(Modifier.padding(vertical = 30.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ){
                DropDown(
                    dropDownTitle = "Audio format",
                    dropDownOptions = listOf("MP3"),
                    onOptionSelected = {
                        println(it)
                    }
                )
                DropDown(
                    dropDownTitle = "Video format",
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
            ){
                if(fileChooserButtonClicked){
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
                ){
                    Text("Save Location")
                }
                Text(text = filePath ?: "Invalid path")

                Divider(Modifier.padding(vertical = 30.dp))

                //Start button - start download
                Button(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    onClick = {
                        myCoroutineScope.launch(Dispatchers.IO) {
                            fef()
                        }
                    }
                ){
                    Text("START")
                }

                DownloadProgress(
                    onDownloadError = { errorMessage ->
                        println(errorMessage)
                    },
                    onDownloadComplete = {
                        println("Download complete")
                    }
                )

            }
        }
    }
}

suspend fun fef(){
    val downloadFolder = "/Users/ife/Documents/mediaDownloader"
    coroutineScope {
        launch(Dispatchers.IO) {
            //youtube-dl --format "mp4" "https://www.youtube.com/watch?v=bhrumYeZvjs"
//            val pwdCommand = ProcessBuilder("pwd")
//            pwdCommand.directory(File(downloadFolder))
//            pwdCommand.redirectOutput(File("${downloadFolder}/output.log"))
//            val pwdCommandProcess = pwdCommand.start()


            val youtubeDlCommand = ProcessBuilder("youtube-dl", "--format", "mp4", "https://www.youtube.com/watch?v=bhrumYeZvjs")
            youtubeDlCommand.directory(File(downloadFolder))
            youtubeDlCommand.redirectOutput(File("${downloadFolder}/downloader.log"))
            val youtubeDlCommandProcess = youtubeDlCommand.start()


//            val fe = ProcessBuilder("youtube-dl", "--format", "mp4", "https://www.youtube.com/watch?v=bhrumYeZvjs").start()
//            val result = println(fe.inputStream)

        }

    }
}

@Composable
@Preview
fun DownloadProgress(
    modifier: Modifier = Modifier,
    percentValue: Int = 0,
    onDownloadComplete: ()->Unit,
    onDownloadError: (errorMessage: String)->Unit,
){
    Text("%s%%".format(percentValue))
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
