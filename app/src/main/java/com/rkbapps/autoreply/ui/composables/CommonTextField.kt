package com.rkbapps.autoreply.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun CommonTextField(
    modifier: Modifier = Modifier,
    text: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    trailingText: String? = null,
    labelText: String? = null,
    placeholderText: String,
    maxLines: Int = Int.MAX_VALUE,
    onTextChange: (text: String) -> Unit = {}
) {

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        labelText?.let { Text(it, style = MaterialTheme.typography.titleMedium) }
        TextField(
            value = text,
            onValueChange = { onTextChange(it) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                focusedPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            maxLines = maxLines,
            modifier = modifier,
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            placeholder = {
                Text(
                    text = placeholderText,
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            trailingIcon = {
                trailingText?.let {
                    Text(text = it,)
                }
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