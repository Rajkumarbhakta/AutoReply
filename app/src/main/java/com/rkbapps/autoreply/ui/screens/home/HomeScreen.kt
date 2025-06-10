package com.rkbapps.autoreply.ui.screens.home

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.rkbapps.autoreply.R
import com.rkbapps.autoreply.navigation.NavigationRoutes.AddEditAutoReply
import com.rkbapps.autoreply.ui.screens.home.composables.PermissionRequiredUi
import com.rkbapps.autoreply.ui.screens.home.composables.PermissionRequiredUiWithSwitch
import com.rkbapps.autoreply.ui.screens.home.composables.RetakePermissionDialog
import com.rkbapps.autoreply.ui.screens.rules.RulesItem


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, viewModel: HomeScreenViewModel = hiltViewModel()) {

    val context = LocalContext.current

    val isServiceRunning = viewModel.isServiceRunning.collectAsStateWithLifecycle()
    val autoReplyList = viewModel.autoReplyList.collectAsStateWithLifecycle()

    val isRequestPermissionOpen = remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { granted ->
                if (granted) {
                    isRequestPermissionOpen.value = false
                    Toast.makeText(context, "Notification permission granted", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        )

    if (isRequestPermissionOpen.value && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        RetakePermissionDialog(onOkClick = {
            takePermission(permissionLauncher)
            isRequestPermissionOpen.value = false
        })
    }


    Scaffold(
        topBar = {
            TopBar()
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            state = listState,
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            item {
                if (viewModel.isNotificationPermissionGranted() != true && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    PermissionRequiredUi(
                        text = "Notification permission",
                        buttonText = "Grant"
                    ) {
                        takePermission(permissionLauncher)
                    }
                }
            }
            item {
                if (!viewModel.isNotificationListenPermissionEnable()) {
                    PermissionRequiredUi(
                        text = "Notification Access", buttonText = "Grant"
                    ) {
                        viewModel.requestNotificationPermission()
                    }
                }

            }
            item {
                PermissionRequiredUiWithSwitch(
                    text = "Auto Reply",
                    subtitle = if (isServiceRunning.value) "Auto replies are currently enabled" else "Auto replies are currently disabled",
                    isChecked = isServiceRunning.value,
                    onCheckedChange = {
                        if (isServiceRunning.value) {
                            viewModel.stopService()
                        } else {
                            viewModel.startService()
                        }
                    }
                )
            }

            item {
                Text(
                    "Active Rules",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            if (autoReplyList.value.isEmpty()) {
                item {
                    Text(
                        "No active rules found",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            items(autoReplyList.value) { rule ->
                RulesItem(rule = rule, onSwitchChange = {
                    viewModel.updateRuleActiveStatus(rule.id, it)
                }) {
                    navController.navigate(AddEditAutoReply(id = it))
                }
            }

        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(modifier: Modifier = Modifier) {
    TopAppBar(
        title = {
            Text(stringResource(id = R.string.app_name))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
    )
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun takePermission(permissionLauncher: ManagedActivityResultLauncher<String, Boolean>) {
    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
}