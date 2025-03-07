package com.rkbapps.autoreply.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
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


@Composable
fun HomeScreen(navController:NavHostController,) {

    val context = LocalContext.current

    val isPermissionGranted = ContextCompat.checkSelfPermission(
        context, Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED

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
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    if (!isPermissionGranted){
                        takePermission(permissionLauncher)
                    }
                }
            }) {
                Text("Take Permission")
            }
            Button(onClick = {
                val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                context.startActivity(intent)
            }) {
                Text("Enable Notification Access")
            }
            Button(onClick = {
                context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            }) {
                Text("Start notification listener")
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