package com.rkbapps.autoreply.screens.home

import android.Manifest
import android.app.AlertDialog
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.EditRoad
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.rkbapps.autoreply.R
import com.rkbapps.autoreply.data.AutoReplyEntity
import com.rkbapps.autoreply.navigation.AddEditAutoReply


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, viewModel: HomeScreenViewModel = hiltViewModel()) {

    val context = LocalContext.current

    val isServiceRunning = viewModel.isServiceRunning.collectAsStateWithLifecycle()
    val autoReplyList = viewModel.autoReplyList.collectAsStateWithLifecycle()
    val replyType = viewModel.replyType.collectAsStateWithLifecycle()

    val isRequestPermissionOpen = remember { mutableStateOf(false) }

    val deletableReply = remember { mutableStateOf<AutoReplyEntity?>(null) }

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

        if (deletableReply.value!=null){
            DeleteConfirmationDialog(
                onDismiss = {
                    deletableReply.value = null
                },
                onConfirm = {
                    deletableReply.value?.let { viewModel.onDeleteClick(it) }
                    deletableReply.value = null
                }
            )
        }


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                PermissionRequiredUiWithSwitch(
                    text = if (isServiceRunning.value) "Stop Service" else "Start Service",
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
                ElevatedCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    ) {
                        Text("Reply Settings", style = MaterialTheme.typography.titleMedium)
                        Text("Configure who receives auto replies", style = MaterialTheme.typography.labelMedium)
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            viewModel.replyTypeList.forEach {
                                RadioButton(
                                    selected = it == replyType.value,
                                    onClick = {
                                        viewModel.changeReplyType(it)
                                    }
                                )
                                Text(it.uppercase())
                            }
                        }

                    }
                }
            }
            item {
                HorizontalDivider()
            }
            item {
                Text("Active Rules", style = MaterialTheme.typography.titleMedium)
                Text("Manage your auto reply configurations", style = MaterialTheme.typography.labelMedium)
            }
            when {
                autoReplyList.value.isLoading -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                autoReplyList.value.isError -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = autoReplyList.value.message ?: "Something went wrong")
                        }
                    }
                }

                autoReplyList.value.data.isNullOrEmpty() -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "No Auto Replies found")
                        }
                    }
                }

                !autoReplyList.value.data.isNullOrEmpty() -> {
                    items(items = autoReplyList.value.data!!, key = {
                        it.id
                    }) {
                        AutoReplyUI(data = it, onEditClick = {
                            viewModel.onEditClick(navController, it)
                        }, onDeleteClick = {
                            deletableReply.value = it
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
    ElevatedCard {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {


            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start)
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color = color.value)

                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

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
                    "${data.matchingType.value} : '${data.receive}'",
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "Response",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium
                )
                Text(data.send, maxLines = 2, overflow = TextOverflow.Ellipsis)
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
fun DeleteConfirmationDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        title = {
            Text("Delete Auto Reply")
        },
        text = {
            Text("Are you sure you want to delete this auto reply? This action cannot be undone.")
        },
        confirmButton = {
            OutlinedButton(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        onDismissRequest = onDismiss
    )
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