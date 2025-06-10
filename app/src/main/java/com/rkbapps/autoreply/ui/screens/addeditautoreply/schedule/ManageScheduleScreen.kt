package com.rkbapps.autoreply.ui.screens.addeditautoreply.schedule

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.rkbapps.autoreply.data.DaysOfWeek
import com.rkbapps.autoreply.data.ReplySchedule
import com.rkbapps.autoreply.data.Time
import com.rkbapps.autoreply.ui.composables.CommonTimePicker
import com.rkbapps.autoreply.ui.screens.addeditautoreply.AddEditAutoReplyScreenViewModel
import com.rkbapps.autoreply.ui.theme.surfaceColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageScheduleScreen(
    navController: NavHostController,
    viewModel: AddEditAutoReplyScreenViewModel
) {
    val rule = viewModel.rule.collectAsStateWithLifecycle().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Schedule") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, "Back Icon")
                    }
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = surfaceColor)
                    .padding(10.dp)
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { navController.navigateUp() }
                ) {
                    Text("Done")
                }
            }
        }
    ) { innerPadding ->

        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            if (rule.schedule != null) {
                item {
                    CommonTimePicker(
                        modifier = Modifier.fillMaxWidth(),
                        pickedTime = rule.schedule.startTime,
                        labelText = "Start Time",
                    ) { hour, minute ->
                        val updatedSchedule = rule.schedule.copy(startTime = Time(hour, minute))
                        viewModel.updateRule(rule.copy(schedule = updatedSchedule))
                    }
                }
                item {
                    CommonTimePicker(
                        modifier = Modifier.fillMaxWidth(),
                        pickedTime = rule.schedule.endTime,
                        labelText = "End Time",
                    ) { hour, minute ->
                        val updatedSchedule = rule.schedule.copy(endTime = Time(hour, minute))
                        viewModel.updateRule(rule.copy(schedule = updatedSchedule))
                    }
                }

                item {
                    Text(
                        "Active Days",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                items(
                    items = DaysOfWeek.entries
                ) {
                    DaysOfWeekItem(
                        day = it,
                        isSelected = rule.schedule.daysOfWeek.contains(it),
                    ) {
                        val updatedDays = if (rule.schedule.daysOfWeek.contains(it)) {
                            rule.schedule.daysOfWeek - it
                        } else {
                            rule.schedule.daysOfWeek + it
                        }
                        val updatedSchedule = rule.schedule.copy(daysOfWeek = updatedDays)
                        viewModel.updateRule(rule.copy(schedule = updatedSchedule))
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Always Active")
                    Switch(
                        checked = rule.schedule == null,
                        onCheckedChange = {
                            val update = if (it) {
                                rule.copy(schedule = null)
                            } else {
                                rule.copy(schedule = ReplySchedule())
                            }
                            viewModel.updateRule(update)
                        }
                    )
                }
            }


        }

    }
}


@Composable
fun DaysOfWeekItem(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    day: DaysOfWeek,
    onDaySelected: (DaysOfWeek) -> Unit = { /* No-op */ }
) {

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = {
                onDaySelected(day)
            }
        )
        Text(
            day.name
        )
    }


}