package com.rkbapps.autoreply.ui.screens.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.rkbapps.autoreply.data.AutoReplyEntity
import com.rkbapps.autoreply.data.PreferenceManager
import com.rkbapps.autoreply.navigation.NavigationRoutes.AddEditAutoReply
import com.rkbapps.autoreply.utils.ReplyType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: HomeScreenRepository,
    private val gson: Gson,
    private val preferenceManager:PreferenceManager
): ViewModel() {

    val isServiceRunning = repository.isServiceRunning

    val autoReplyList = repository.autoReplyList

    val replyTypeList = listOf(
        ReplyType.INDIVIDUAL,
        ReplyType.GROUP,
        ReplyType.BOTH
    )

    val replyType = preferenceManager.replyTypeFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, ReplyType.INDIVIDUAL)

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


    fun changeReplyType(type:String){
        viewModelScope.launch {
            preferenceManager.changeReplyType(type)
        }
    }





}
