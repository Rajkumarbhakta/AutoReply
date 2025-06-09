package com.rkbapps.autoreply.ui.screens.rules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.rkbapps.autoreply.navigation.NavigationRoutes
import com.rkbapps.autoreply.ui.composables.CommonSearchBar
import okhttp3.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoReplyRulesScreen(navController: NavHostController) {

    val searchQuery = remember { mutableStateOf("") }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Auto Reply Rules")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                actions = {
                    IconButton(onClick = {
                        navController.navigate(NavigationRoutes.AddEditAutoReply())
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Rule",
                        )
                    }
                }
            )
        }
    ) {innerPadding ->

        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {

            item {
                CommonSearchBar(
                    modifier = Modifier.fillMaxWidth(),
                    query = searchQuery.value) {
                    searchQuery.value = it
                }
            }

            items(30) {
                RulesItem()
            }
            
        }

    }

}


@Preview
@Composable
fun RulesItem(modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
        ) {
        Column {
            Text("Greeting Rule", style = MaterialTheme.typography.titleMedium)
            Text("Target: Individual", style = MaterialTheme.typography.bodySmall)
            Text("Trigger: Contains 'Hello'",style = MaterialTheme.typography.bodySmall)
            Text("Reply: Hi there! How can I help?",style = MaterialTheme.typography.bodySmall)
        }
        Switch(
            checked = true,
            onCheckedChange = {}
        )
    }
}