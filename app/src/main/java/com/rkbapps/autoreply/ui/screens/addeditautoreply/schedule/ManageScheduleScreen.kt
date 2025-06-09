package com.rkbapps.autoreply.ui.screens.addeditautoreply.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.rkbapps.autoreply.data.DaysOfWeek
import com.rkbapps.autoreply.ui.composables.CommonTimePicker
import com.rkbapps.autoreply.ui.theme.surfaceColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageScheduleScreen(navController: NavHostController) {
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
                modifier = Modifier.fillMaxWidth()
                    .background(color = surfaceColor)
                    .padding(10.dp)
            ){
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {}
                ) {
                    Text("Done")
                }
            }
        }
    ) { innerPadding ->


        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            item {
                CommonTimePicker(
                    modifier = Modifier.fillMaxWidth(),
                    labelText = "Start Time",
                )
            }
            item {
                CommonTimePicker(
                    modifier = Modifier.fillMaxWidth(),
                    labelText = "End Time",
                )
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
                DaysOfWeekItem(day = it)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Always Active")
                    Switch(
                        checked = true,
                        onCheckedChange = {}
                    )
                }
            }


        }

    }
}


@Composable
fun DaysOfWeekItem(modifier: Modifier = Modifier,day: DaysOfWeek) {

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = true,
            onCheckedChange = {}
        )
        Text(
            day.name
        )
    }


}