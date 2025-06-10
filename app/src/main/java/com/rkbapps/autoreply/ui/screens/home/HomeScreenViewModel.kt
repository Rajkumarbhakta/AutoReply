package com.rkbapps.autoreply.ui.screens.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rkbapps.autoreply.data.PreferenceManager
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
): ViewModel() {

    val isServiceRunning = repository.isServiceRunning

    val autoReplyList = repository.autoReplyList.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )


    fun updateRuleActiveStatus(id: Int, isActive: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateRuleActiveStatus(id, isActive)
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


}
