package com.rkbapps.autoreply.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rkbapps.autoreply.ui.theme.textFieldBackGroundLight
import com.rkbapps.autoreply.ui.theme.textFieldPlaceHolderColor


@Composable
fun CommonTextField(
    modifier: Modifier = Modifier,
    text: String,
    labelText: String,
    placeholderText: String,
    maxLines: Int = Int.MAX_VALUE,
    onTextChange: (text: String) -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(labelText, style = MaterialTheme.typography.titleMedium)
        TextField(
            value = text,
            onValueChange = { onTextChange(it) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = textFieldBackGroundLight,
                unfocusedContainerColor = textFieldBackGroundLight,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            maxLines = maxLines,
            modifier = modifier,
            shape = RoundedCornerShape(10.dp),
            placeholder = {
                Text(
                    text = placeholderText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textFieldPlaceHolderColor
                )
            }

        )
    }
}

@Preview(showBackground = true)
@Composable
fun CommonTextFieldPreview() {
    CommonTextField(
        text = "",
        labelText = "Label",
        placeholderText = "Placeholder",
        onTextChange = {}
    )
}