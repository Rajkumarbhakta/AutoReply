package com.rkbapps.autoreply.ui.screens.addeditautoreply

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.google.gson.Gson
import com.rkbapps.autoreply.data.AutoReplyEntity
import com.rkbapps.autoreply.navigation.NavigationRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditAutoReplyScreenViewModel @Inject constructor(
    private val repository: AddEditAutoReplyScreenRepository,
    saveStateHandle: SavedStateHandle,
    private val gson: Gson
): ViewModel() {

    fun isContactPermissionGranted(context: Context): Boolean? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            null
        }
    }

    val contacts = repository.contactsState

    fun loadContacts() = viewModelScope.launch(Dispatchers.IO) {
        repository.loadContacts()
    }



    val matchingTypeList = repository.matchingTypeList

    val autoReplyAddStatus = repository.autoReplyAddStatus

    val data = saveStateHandle.toRoute<NavigationRoutes.AddEditAutoReply>()

    val autoReplyObject = if(data.data != null){
            gson.fromJson(data.data, AutoReplyEntity::class.java)
        }else{ null }

    val ruleState = repository.ruleState

    fun addNewAutoReply(autoReplyEntity: AutoReplyEntity, addEditType: AddEditType = AddEditType.ADD){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addNewAutoReply(autoReplyEntity,addEditType)
        }
    }

    fun updateRule(rule: AutoReplyEntity) = repository.updateRule(rule)





    companion object{
        const val MAX_CHARACTER_LIMIT = 160
    }


}