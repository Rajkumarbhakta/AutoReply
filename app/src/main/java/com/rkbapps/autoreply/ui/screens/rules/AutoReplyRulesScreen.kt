package com.rkbapps.autoreply.ui.screens.rules

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.rkbapps.autoreply.data.AutoReplyEntity
import com.rkbapps.autoreply.navigation.NavigationRoutes
import com.rkbapps.autoreply.ui.composables.CommonSearchBar
import okhttp3.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoReplyRulesScreen(
    navController: NavHostController,
    viewModel: AutoReplyRulesViewModel = hiltViewModel()
) {
    val rules = viewModel.autoReplyRules.collectAsStateWithLifecycle().value
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
                    Button(onClick = {
                        navController.navigate(NavigationRoutes.AddEditAutoReply())
                    },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.onBackground
                        )
                        ) {
                        Row (
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Rule",
                            )
                            Text("Add")
                        }
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

            if (rules.isEmpty()){
                item {
                    Text(
                        "No rules found",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 20.dp),
                    )
                }
            }

            items(items = rules, key = { it.id }) { rule ->
                RulesItem(
                    rule = rule,
                    onSwitchChange = {
                        val update = rule.copy(isActive = it)
                        viewModel.updateRule(update)
                    }
                ) {
                    navController.navigate(NavigationRoutes.AddEditAutoReply(id = it))
                }
            }
            
        }
    }

}


@Composable
fun RulesItem(
    modifier: Modifier = Modifier,
    rule: AutoReplyEntity,
    onSwitchChange: (Boolean) -> Unit = { /*TODO*/ },
    onItemClick:(Int)-> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onItemClick(rule.id)
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = modifier
                .weight(1f)
                .padding(end = 10.dp)
        ) {
            Text(rule.name, style = MaterialTheme.typography.titleMedium)
            Text("Target: ${rule.replyType.name}", style = MaterialTheme.typography.bodySmall)
            Text(
                "Trigger: ${rule.matchingType.value} '${rule.trigger}'",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "Reply: ${rule.reply}",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Switch(
            checked = rule.isActive,
            onCheckedChange = onSwitchChange
        )
    }
}