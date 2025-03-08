package com.rkbapps.autoreply.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.notifyhistory.domain.manager.PermissionManagerImpl
import com.notifyhistory.domain.manager.PermissionManagerImpl.Companion.EXTRA_FRAGMENT_ARG_KEY
import com.notifyhistory.domain.manager.PermissionManagerImpl.Companion.EXTRA_SHOW_FRAGMENT_ARGUMENTS
import com.rkbapps.autoreply.BuildConfig
import com.rkbapps.autoreply.services.MyNotificationListenerService
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rkbapps.autoreply.services.KeepAliveService


@Composable
fun HomeScreen(navController: NavHostController,viewModel: HomeScreenViewModel = hiltViewModel()) {
    val isServiceRunning = viewModel.isServiceRunning.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val isRequestPermissionOpen = remember { mutableStateOf(false) }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { granted ->
                if (granted) {
                    isRequestPermissionOpen.value = false
                    Toast.makeText(context, "Notification permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        )

    if (isRequestPermissionOpen.value && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        RetakePermissionDialog(onOkClick = {
            takePermission(permissionLauncher)
            isRequestPermissionOpen.value = false
        })
    }


    Scaffold { innerPadding->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                viewModel.isNotificationPermissionGranted()?.let {
                    if (!it) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            takePermission(permissionLauncher)
                        }
                    }
                }
            }) {
                Text("Take Permission")
            }
            Button(onClick = {
                viewModel.requestNotificationPermission()
            }) {
                if(viewModel.isNotificationListenPermissionEnable()){
                    Text("Notification Access Enabled")
                }else{
                    Text("Enable Notification Access")
                }

            }
            Button(onClick = {
                if(isServiceRunning.value){
                    viewModel.stopService()
                }else{
                    viewModel.startService()
                }
            }) {
                if(isServiceRunning.value){
                    Text("Stop Service")
                }else{
                    Text("Start Service")
                }
            }
        }
    }

}


@Composable
fun RetakePermissionDialog(modifier: Modifier = Modifier,onOkClick:()->Unit) {
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