package com.rkbapps.autoreply.ui.screens.home

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.rkbapps.autoreply.R
import com.rkbapps.autoreply.data.AutoReplyEntity
import com.rkbapps.autoreply.navigation.NavigationRoutes.AddEditAutoReply
import com.rkbapps.autoreply.ui.screens.home.composables.AutoReplyUI
import com.rkbapps.autoreply.ui.screens.home.composables.DeleteConfirmationDialog
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
    val replyType = viewModel.replyType.collectAsStateWithLifecycle()

    val isRequestPermissionOpen = remember { mutableStateOf(false) }
    val deletableReply = remember { mutableStateOf<AutoReplyEntity?>(null) }
    val listState = rememberLazyListState()

    val permissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(),
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

        if (deletableReply.value != null) {
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
            state = listState,
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            item {
                if (viewModel.isNotificationPermissionGranted() == true && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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
                ElevatedCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    ) {
                        Text("Reply Settings", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Configure who receives auto replies",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            viewModel.replyTypeList.forEach {
                                RadioButton(
                                    selected = it.name == replyType.value,
                                    onClick = {
                                        viewModel.changeReplyType(it.name)
                                    }
                                )
                                Text(it.name.uppercase())
                            }
                        }

                    }
                }
            }
            item {
                HorizontalDivider()
            }
            item {
                Text("Active Rules",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
                Text(
                    "Manage your auto reply configurations",
                    style = MaterialTheme.typography.titleSmall
                )
            }

            items(count = 20,) {
//                RulesItem()
            }

           /* when {
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
                    *//*items(items = autoReplyList.value.data!!, key = {
                        it.id
                    }) {
                        AutoReplyUI(data = it, onEditClick = {
                            viewModel.onEditClick(navController, it)
                        }, onDeleteClick = {
                            deletableReply.value = it
                        })
                    }*//*


                }
            }*/
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