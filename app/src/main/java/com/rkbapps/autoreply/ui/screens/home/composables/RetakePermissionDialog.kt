package com.rkbapps.autoreply.ui.screens.home.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun RetakePermissionDialog(modifier: Modifier = Modifier, onOkClick: () -> Unit) {
    AlertDialog(
        text = {
            Text("Notification permission required for this feature to work")
        },
        title = {
            Text("Permission required")
        },
        confirmButton = {
            TextButton(onClick = {
                onOkClick()
            }) {
                Text("Ok")
            }
        },
        onDismissRequest = {},
        modifier = modifier
    )
}
