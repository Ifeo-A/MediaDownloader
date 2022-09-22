package components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
@Preview
fun DropDown(
    modifier: Modifier = Modifier,
    dropDownTitle: String = "",
    dropDownOptions: List<String>,
    onOptionSelected: (optionText: String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box() {
            Button(
                onClick = { expanded = true },
            ) {
                Text(text = dropDownTitle)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                dropDownOptions.forEach { optionText ->
                    DropdownMenuItem(
                        onClick = {
                            onOptionSelected(optionText)
                            expanded = false
                            selectedText = optionText
                        },
                    ) {
                        Text(
                            text = optionText,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth())

                    }
                }
            }
        }
        Text(selectedText, textAlign = TextAlign.Center)
    }
}