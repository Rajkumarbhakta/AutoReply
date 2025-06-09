package com.rkbapps.autoreply.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.rkbapps.autoreply.ui.theme.textFieldBackGroundLight
import com.rkbapps.autoreply.ui.theme.textFieldPlaceHolderColor


@Composable
fun CommonSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    placeholderText: String = "Search",
    onQueryChange: (String) -> Unit,
) {
    TextField(
        value = query,
        onValueChange = { onQueryChange(it) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = textFieldBackGroundLight,
            unfocusedContainerColor = textFieldBackGroundLight,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        singleLine = true,
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        placeholder = {
            Text(
                text = placeholderText,
                style = MaterialTheme.typography.bodyMedium,
                color = textFieldPlaceHolderColor
            )
        },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, "Search Icon")
        },
        trailingIcon = {
            AnimatedVisibility(query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear Search",
                    )
                }
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                onQueryChange(query)
            }
        )

    )

}