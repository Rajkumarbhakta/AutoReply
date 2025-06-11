package com.rkbapps.autoreply.ui.screens.home.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PermissionRequiredUiWithSwitch(
    modifier: Modifier = Modifier,
    text: String,
    subtitle: String = "Auto replies are currently enabled",
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val color = animateColorAsState(
        if (isChecked) Color.Green else Color.Red
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text,
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.labelMedium,
            )
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}