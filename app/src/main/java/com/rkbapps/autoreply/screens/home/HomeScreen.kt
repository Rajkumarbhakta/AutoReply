package com.rkbapps.autoreply.screens.home

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.EditRoad
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.rkbapps.autoreply.R
import com.rkbapps.autoreply.data.AutoReplyEntity
import com.rkbapps.autoreply.navigation.AddEditAutoReply
import java.nio.file.WatchEvent


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, viewModel: HomeScreenViewModel = hiltViewModel()) {

    val context = LocalContext.current

    val isServiceRunning = viewModel.isServiceRunning.collectAsStateWithLifecycle()

    val autoReplyList = viewModel.autoReplyList.collectAsStateWithLifecycle()


    val isRequestPermissionOpen = remember { mutableStateOf(false) }

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
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.app_name))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    )

            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(route = AddEditAutoReply())
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "add new auto reply")
            }
        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(vertical = 8.dp, horizontal = 16.dp),

            ) {

            if (viewModel.isNotificationPermissionGranted() == false && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                PermissionRequiredUi(
                    text = "Notification permission",
                    buttonText = "Grant"
                ) {
                    takePermission(permissionLauncher)
                }
            }
            if (!viewModel.isNotificationListenPermissionEnable()) {
                PermissionRequiredUi(
                    text = "Notification Access", buttonText = "Grant"

                ) {
                    viewModel.requestNotificationPermission()
                }
            }
            PermissionRequiredUi(
                text = if (isServiceRunning.value) "Stop Service" else "Start Service",
                buttonText = if (isServiceRunning.value) "Stop" else "Start"
            ) {
                if (isServiceRunning.value) {
                    viewModel.stopService()
                } else {
                    viewModel.startService()
                }
            }
            Spacer(Modifier.height(5.dp))
            HorizontalDivider()
            Spacer(Modifier.height(5.dp))
            if (autoReplyList.value.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            if (autoReplyList.value.isError) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = autoReplyList.value.message ?: "Something went wrong")
                }
            }

            if (autoReplyList.value.data != null && !autoReplyList.value.isLoading && !autoReplyList.value.isError) {
                LazyColumn {
                    items(items = autoReplyList.value.data!!, key = {
                        it.id
                    }) {
                        AutoReplyUI(data = it, onEditClick = {
                            viewModel.onEditClick(navController,it)
                        },onDeleteClick ={
                            viewModel.onDeleteClick(it)
                        })
                    }
                }
            }

        }
    }

}


@Composable
fun PermissionRequiredUi(
    modifier: Modifier = Modifier,
    text: String,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text)
        OutlinedButton(
            onClick = onButtonClick
        ) {
            Text(buttonText)
        }
    }
}

@Composable
fun AutoReplyUI(modifier: Modifier = Modifier, data: AutoReplyEntity,onEditClick:(AutoReplyEntity)-> Unit,onDeleteClick:(AutoReplyEntity)-> Unit) {
    Card(
        modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Column(modifier.weight(1f)) {
                Row {
                    Text("Receive: ", style = MaterialTheme.typography.titleMedium)
                    Text(data.receive)
                }
                Row {
                    Text("Send: ",style = MaterialTheme.typography.titleMedium)
                    Text(data.send)
                }
                Row {
                    Text("Matching Type: ",style = MaterialTheme.typography.titleMedium)
                    Text(data.matchingType.name)
                }
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


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun takePermission(permissionLauncher: ManagedActivityResultLauncher<String, Boolean>) {
    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
}