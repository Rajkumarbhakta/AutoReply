package com.rkbapps.autoreply.ui.screens.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.rkbapps.autoreply.data.AutoReplyDao
import com.rkbapps.autoreply.manager.PermissionManager
import com.rkbapps.autoreply.services.KeepAliveService
import javax.inject.Inject

class HomeScreenRepository @Inject constructor(
    private val permissionManager: PermissionManager,
    private val dataBase: AutoReplyDao
) {

    val isServiceRunning = KeepAliveService.isRunning


    val autoReplyList = dataBase.getActiveAutoReplies()


    suspend fun updateRuleActiveStatus(id: Int, isActive: Boolean) {
        dataBase.updateAutoReplyActiveStatus(id, isActive)
    }


    fun startService(context: Context) {
        val intent = Intent(context, KeepAliveService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun stopService(context: Context) {
        val intent = Intent(context, KeepAliveService::class.java)
        context.stopService(intent)
    }


    fun isNotificationPermissionGranted(context: Context): Boolean? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            null
        }
    }

    fun requestNotificationPermission() = permissionManager.requestNotificationPermission()

    fun isNotificationListenPermissionEnable(): Boolean =
        permissionManager.isNotificationPermissionGranted()


}