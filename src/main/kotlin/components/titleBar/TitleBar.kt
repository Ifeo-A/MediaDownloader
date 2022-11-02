package components.titleBar

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import theme.black

@Preview
@Composable
fun TitleBar(
    onMinimizeClicked: () -> Unit = {},
    onMaximizeClicked: () -> Unit = {},
    onCloseClicked: () -> Unit = {}
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 2.dp, color = black)
            .padding(4.dp),
        horizontalArrangement = Arrangement.End
    ) {
        WindowVisibilityControlButton(
            modifier = Modifier.padding(horizontal = 14.dp),
            buttonType = ButtonType.Maximize,
            imageName = "maximize.png",
            onClick = { onMaximizeClicked() }
        )
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
}

@Preview
@Composable
fun TitleBarPreview() {
    Row(
        modifier = Modifier
            .background(color = black.copy(alpha = 0.8f))
            .fillMaxSize()
    ) {
        TitleBar()
    }
}