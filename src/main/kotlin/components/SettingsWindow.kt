package components

import WINDOW_HEIGHT
import WINDOW_WIDTH
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import theme.lightPurple
import theme.lightRed
import theme.orange
import theme.yellow
import util.Constants.Settings.SETTINGS_WINDOW_TITLE
import util.SettingsUtil

@Preview
@Composable
fun SettingsWindow(
    windowClose: () -> Unit,
    downloadChooserButtonClicked: () -> Unit,
    downloadDirectoryChanged: (filePath: String) -> Unit
) {

    var downloadDirectory by remember { mutableStateOf(SettingsUtil.readDownloadLocationFromSettingsFile() ?: "") }
    var openFileChooser by remember { mutableStateOf(false) }

    Window(
        title = SETTINGS_WINDOW_TITLE,
        onCloseRequest = { windowClose() },
        state = rememberWindowState(width = WINDOW_WIDTH, height = WINDOW_HEIGHT)
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush
                        .linearGradient(listOf(lightRed, lightPurple))
                )
                .padding(horizontal = 32.dp, vertical = 24.dp)

        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                MyText(
                    text = "Default download location: ",
                    fontSize = 18.sp,
                    color = Color.White,
                    modifier = Modifier
                        .padding(start = 12.dp)
                )
                TextField(
                    value = downloadDirectory,
                    onValueChange = { downloadDirectory = it },
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 18.sp
                    ),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(
                            onClick = { openFileChooser = true },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Folder,
                                contentDescription = "",
                                tint = Color.White,
                                modifier = Modifier
                                    .width(50.dp)
                            )
                        }
                    },
                    shape = CircleShape,
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = Color.White,
                        disabledTextColor = Color.Transparent,
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .width(600.dp)
                        .height(Dp.Unspecified)
                        .border(
                            border = BorderStroke(
                                width = 1.dp,
                                brush = Brush
                                    .linearGradient(listOf(orange, yellow))
                            ),
                            shape = CircleShape
                        ),
                )
            }
        }

        if (openFileChooser) {
            FilePicker(
                onCloseRequest = { _filePath ->
                    openFileChooser = false
                    println("File: $_filePath")
                    _filePath?.let {
                        downloadDirectory = it
                        SettingsUtil.updateDownloadDirectory(downloadDirectory = it)
                        downloadDirectoryChanged(it)
                    }
                },
                onError = { errorMessage ->
                    openFileChooser = false
                    println("Error: $errorMessage")
                }
            )
        }
    }
}

data class SettingsOptions(
    val downloadLocation: String? = null,
    val settingsFileLocation: String? = null
)