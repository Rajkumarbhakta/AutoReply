package com.rkbapps.autoreply.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.rkbapps.autoreply.data.Time
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonTimePicker(
    modifier: Modifier = Modifier,
    pickedTime: Time? = null,
    labelText: String,
    onTimeChange: (hour: Int, minute: Int) -> Unit = { _, _ -> },
) {
    val isTimePickerVisible = remember { mutableStateOf(false) }

    if (isTimePickerVisible.value) {
        TimePickerDialog(
            onDismissRequest = { isTimePickerVisible.value = false },
            listener = { state ->
                val hour = state.hour
                val minute = state.minute
                onTimeChange(hour, minute)
            }
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(labelText, style = MaterialTheme.typography.titleMedium)
        TextField(
            value = if (pickedTime == null) "" else hourMinuteToString(
                pickedTime.hour,
                pickedTime.minute
            ),
            enabled = false,
            onValueChange = { },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            singleLine = true,
            modifier = modifier,
            shape = RoundedCornerShape(10.dp),
            placeholder = {
                Text(
                    text = "00:00 AM",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        isTimePickerVisible.value = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Time Picker Icon"
                    )
                }
            }
        )
    }
}


fun hourMinuteToString(hour: Int, minute: Int): String {
    val amPm = if (hour < 12) "AM" else "PM"
    val adjustedHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
    return String.format(Locale.getDefault(), "%02d:%02d %s", adjustedHour, minute, amPm)
}


@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {},
    listener: (state: TimePickerState) -> Unit = {}
) {

    val sate = rememberTimePickerState()

    LaunchedEffect(sate.hour, sate.minute) {
        listener(sate)
    }

    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Column {
            TimePicker(
                modifier = modifier
                    .background(color = MaterialTheme.colorScheme.surface)
                    .padding(10.dp),
                state = sate,
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CommonTimePickerPreview() {
    CommonTimePicker(
        labelText = "Label",
    )
}