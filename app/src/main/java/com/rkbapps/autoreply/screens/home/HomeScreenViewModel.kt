package com.rkbapps.autoreply.screens.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.rkbapps.autoreply.data.AutoReplyEntity
import com.rkbapps.autoreply.navigation.AddEditAutoReply
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: HomeScreenRepository,
    private val gson: Gson
): ViewModel() {

    val isServiceRunning = repository.isServiceRunning

    val autoReplyList = repository.autoReplyList



    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllAutoReply()
        }
    }


    fun startService(){
        repository.startService(context = context)
    }
    fun stopService(){
        repository.stopService(context = context)
    }

    fun isNotificationPermissionGranted() = repository.isNotificationPermissionGranted(context)
    fun requestNotificationPermission() = repository.requestNotificationPermission()
    fun isNotificationListenPermissionEnable() = repository.isNotificationListenPermissionEnable()

    fun onEditClick(navController: NavHostController, data: AutoReplyEntity){
        navController.navigate(route = AddEditAutoReply(gson.toJson(data)))
    }

    fun onDeleteClick(data: AutoReplyEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAutoReply(data)
        }
    }


}
