package components.titleBar

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import theme.black

sealed class ButtonType {
    object Minimize : ButtonType()
    object Maximize : ButtonType()
    object Close : ButtonType()
}

@Preview
@Composable
fun WindowVisibilityControlButton(
    modifier: Modifier = Modifier,
    buttonType: ButtonType,
    imageName: String,
    onClick: () -> Unit = {},
    contentDescription: String = "Window control button"
) {

    Box(
        modifier = Modifier
            .padding(4.dp)
            .border(width = 2.dp, color = black, shape = CircleShape)
            .padding(8.dp),
    ) {
        Image(
            painterResource(resourcePath = "images/${imageName}"),
            contentDescription = "logo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(25.dp)
                .padding(4.dp)
                .clickable { onClick() }
        )

    }

}

@Preview
@Composable
private fun WindowVisibilityControlButtonClosePreview() {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = black.copy(alpha = 0.8f)
    ) {
        Row {
            WindowVisibilityControlButton(
                imageName = "close.png",
                buttonType = ButtonType.Close
            )
        }
    }

}

@Preview
@Composable
private fun WindowVisibilityControlButtonMinimizePreview() {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = black.copy(alpha = 0.8f)
    ) {
        Row {
            WindowVisibilityControlButton(
                imageName = "minimize.png",
                buttonType = ButtonType.Minimize
            )
        }
    }

}

@Preview
@Composable
private fun WindowVisibilityControlButtonMaximizePreview() {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = black.copy(alpha = 0.8f)
    ) {
        Row {
            WindowVisibilityControlButton(
                imageName = "maximize.png",
                buttonType = ButtonType.Maximize
            )
        }
    }

}