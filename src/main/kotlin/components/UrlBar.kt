package components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import theme.black
import theme.orange
import theme.transparentWhite
import theme.yellow
import util.Constants

@Preview
@Composable
fun UrlBar(downloadUrl: String, urlBarTextContent: (url: String) -> Unit) {
    Row(
        modifier = Modifier
    ) {
        // URL bar
        TextField(
            modifier = Modifier
                .weight(1f)
                .border(
                    border = BorderStroke(
                        width = 1.dp,
                        brush = Brush.linearGradient(listOf(orange, yellow))
                    ),
                    shape = CircleShape
                ),
            value = downloadUrl,
            singleLine = true,
            onValueChange = {
                urlBarTextContent(it)
            },
            placeholder = {
                MyText(Constants.URL_PLACEHOLDER)
            },
            shape = CircleShape,
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.White,
                disabledTextColor = Color.Transparent,
                backgroundColor = transparentWhite,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = Color.White
            )
        )
    }
}

@Composable
@Preview
private fun UrlBarPreview() {
    Row(
        modifier = Modifier
            .background(color = black)
            .fillMaxSize()
    ) {
        UrlBar("download/url") {}
    }
}