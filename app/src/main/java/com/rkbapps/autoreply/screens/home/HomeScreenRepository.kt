package com.rkbapps.autoreply.screens.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.rkbapps.autoreply.data.AutoReplyDao
import com.rkbapps.autoreply.data.AutoReplyEntity
import com.rkbapps.autoreply.manager.PermissionManager
import com.rkbapps.autoreply.services.KeepAliveService
import com.rkbapps.autoreply.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class HomeScreenRepository @Inject constructor(
    private val permissionManager: PermissionManager,
    private val dataBase: AutoReplyDao
) {

    val isServiceRunning = KeepAliveService.isRunning


    private val _autoReplyList = MutableStateFlow(UiState<List<AutoReplyEntity>>())
    val autoReplyList = _autoReplyList.asStateFlow()




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



    fun isNotificationPermissionGranted(context: Context): Boolean?{
       return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
           ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        }else{
            null
       }
    }

    fun requestNotificationPermission() = permissionManager.requestNotificationPermission()

    fun isNotificationListenPermissionEnable(): Boolean = permissionManager.isNotificationPermissionGranted()

    suspend fun getAllAutoReply() {
        _autoReplyList.emit(UiState(isLoading = true))
        try {
            val autoReplyList = dataBase.getAllAutoReplies()
           autoReplyList.collect {
               _autoReplyList.emit(UiState(data = it))
           }
        }catch (e: Exception){
            _autoReplyList.emit(UiState(isError = true, message = e.localizedMessage))
        }
    }

    suspend fun deleteAutoReply(data: AutoReplyEntity){
        try {
            dataBase.deleteAutoReply(data)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }



}