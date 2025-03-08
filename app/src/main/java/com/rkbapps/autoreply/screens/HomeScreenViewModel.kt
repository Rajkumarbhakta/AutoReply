package com.rkbapps.autoreply.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: HomeScreenRepository
): ViewModel() {

    val isServiceRunning = repository.isServiceRunning

    fun startService(){
        repository.startService(context = context)
    }
    fun stopService(){
        repository.stopService(context = context)
    }

    fun isNotificationPermissionGranted() = repository.isNotificationPermissionGranted(context)
    fun requestNotificationPermission() = repository.requestNotificationPermission()
    fun isNotificationListenPermissionEnable() = repository.isNotificationListenPermissionEnable()
}