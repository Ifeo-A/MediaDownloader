package components

import androidx.compose.runtime.*
import androidx.compose.ui.window.AwtWindow
import util.Constants.USER_HOME
import java.awt.FileDialog
import java.awt.Frame

/**
 * Written by caknucle on reddit and modified slightly
 * https://www.reddit.com/r/Kotlin/comments/n16u8z/comment/h0d7ybd/?utm_source=share&utm_medium=web2x&context=3
 */
@Composable
fun FilePicker(
    parent: Frame? = null,
    onCloseRequest: (result: String?) -> Unit,
    onError: (errorMessage: String?) -> Unit
) {
    var fileSelected by remember { mutableStateOf("") }

    val fileDialog = object : FileDialog(
        parent,
        "Choose a folder",
        SAVE
    ) {
        override fun setVisible(value: Boolean) {
            super.setVisible(value)
            if (value) {
                try {
                    fileSelected = directory
                    if (!file.contains("null") && fileSelected.isNotEmpty()) {
                        onCloseRequest(fileSelected)
                        println("Selected directory: ${directory}")
                    } else {
                        onCloseRequest("Invalid path")
                    }

                } catch (e: NullPointerException) {
                    onError(e.message)
                }
            }
        }
    }

    return AwtWindow(
        create = { fileDialog },
        dispose = FileDialog::dispose
    )
}