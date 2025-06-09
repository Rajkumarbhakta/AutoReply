package com.rkbapps.autoreply.ui.screens.home.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.EditRoad
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rkbapps.autoreply.data.AutoReplyEntity

@Composable
fun AutoReplyUI(
    modifier: Modifier = Modifier,
    data: AutoReplyEntity,
    onEditClick: (AutoReplyEntity) -> Unit,
    onDeleteClick: (AutoReplyEntity) -> Unit
) {
    Card(
        modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(modifier.weight(1f)) {
                Text(
                    "Trigger",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    "${data.matchingType.value} : '${data.trigger}'",
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "Response",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium
                )
                Text(data.reply, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            IconButton(
                onClick = {
                    onEditClick(data)
                }
            ) {
                Icon(Icons.Outlined.EditRoad, contentDescription = "edit")
            }
            IconButton(
                onClick = {
                    onDeleteClick(data)
                }
            ) {
                Icon(Icons.Outlined.DeleteSweep, contentDescription = "delete")
            }
        }
    }
}
