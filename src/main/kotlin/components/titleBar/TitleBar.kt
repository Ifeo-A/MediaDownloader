package components.titleBar

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import theme.black
import theme.darkPurple
import theme.darkRed

@Preview
@Composable
fun TitleBar(
    modifier: Modifier = Modifier,
    windowState: WindowState?,
    onMinimizeClicked: () -> Unit,
    onMaximizeClicked: () -> Unit,
    onCloseClicked: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush
                    .linearGradient(listOf(darkRed, darkPurple))
            ),
        horizontalArrangement = Arrangement.End
    ) {

        windowState?.let {
            if (it.placement == WindowPlacement.Fullscreen) {
                WindowVisibilityControlButton(
                    buttonType = ButtonType.Minimize,
                    imageName = "minimize.png",
                    onClick = { onMinimizeClicked() }
                )
                WindowVisibilityControlButton(
                    buttonType = ButtonType.Close,
                    imageName = "close.png",
                    onClick = { onCloseClicked() }
                )
            }

            if (it.placement == WindowPlacement.Floating) {
                WindowVisibilityControlButton(
                    buttonType = ButtonType.Minimize,
                    imageName = "minimize.png",
                    onClick = { onMinimizeClicked() }
                )
                WindowVisibilityControlButton(
                    buttonType = ButtonType.Maximize,
                    imageName = "maximize.png",
                    onClick = { onMaximizeClicked() }
                )
                WindowVisibilityControlButton(
                    buttonType = ButtonType.Close,
                    imageName = "close.png",
                    onClick = { onCloseClicked() }
                )
            }
        }
    }
}

@Preview
@Composable
fun TitleBarPreview() {
    Row(
        modifier = Modifier
            .background(color = black.copy(alpha = 0.8f))
            .fillMaxSize()
    ) {
        TitleBar(windowState = null, onMinimizeClicked = {}, onMaximizeClicked = {}, onCloseClicked = {})
    }
}