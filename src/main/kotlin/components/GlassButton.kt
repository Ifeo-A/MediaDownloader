package components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIconDefaults
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import theme.*

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun GlassButton(
    buttonText: String = "",
    modifier: Modifier = Modifier
        .wrapContentWidth()
        .pointerHoverIcon(icon = PointerIconDefaults.Hand)
        .size(width = 200.dp, height = 80.dp)
        .padding(horizontal = 16.dp, vertical = 8.dp),
    onButtonClick: () -> Unit = {},
) {
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent, contentColor = Color.White),
        contentPadding = PaddingValues(),
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.linearGradient(listOf(orange.copy(alpha = 0.5f), yellow.copy(alpha = 0.5f)))
        ),
        shape = RoundedCornerShape(45.dp),
        onClick = { onButtonClick() }
    ) {
        MyText(text = buttonText)
    }
}

@Composable
@Preview
private fun GlassButtonPreview() {

    Row(
        modifier = Modifier
            .background(color = black.copy(alpha = 0.8f))
            .fillMaxSize()
    ) {
        GlassButton(
            "Button",
        )
    }

}