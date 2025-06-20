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
import com.rkbapps.autoreply.data.AutoReplyEntity
import com.rkbapps.autoreply.navigation.NavigationRoutes
import com.rkbapps.autoreply.utils.ChooseContactType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditAutoReplyScreenViewModel @Inject constructor(
    private val repository: AddEditAutoReplyScreenRepository,
    saveStateHandle: SavedStateHandle,
) : ViewModel() {

    fun isContactPermissionGranted(context: Context): Boolean? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            null
        }
    }

    val data = saveStateHandle.toRoute<NavigationRoutes.AddEditAutoReply>()

    private var contactChooseType: ChooseContactType? = null
    val rule = repository.ruleState

    val contacts = repository.contactsState

    val ruleAddUpdateStatus = repository.ruleAddUpdateStatus


    init {
        viewModelScope.launch {
            data.id?.let {
                repository.loadRule(it)
            }
        }
    }


    fun setContactChooseType(type: ChooseContactType) {
        contactChooseType = type
    }


    fun getContactChooseType(): ChooseContactType {
        return contactChooseType!!
    }

    fun loadIncludeContacts() = viewModelScope.launch(Dispatchers.IO) {
        repository.loadContacts()
        repository.loadIncludeContacts()
    }

    fun loadExcludeContacts() = viewModelScope.launch(Dispatchers.IO) {
        repository.loadContacts()
        repository.loadExcludeContacts()
    }

    fun searchContacts(query: String) = viewModelScope.launch(Dispatchers.IO) {
        contactChooseType?.let {
            repository.searchContacts(query, it)
        }
    }


    fun addNewAutoReply(
        autoReplyEntity: AutoReplyEntity,
        addEditType: AddEditType = if (data.id == null) AddEditType.ADD else AddEditType.EDIT
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addNewAutoReply(autoReplyEntity, addEditType)
        }
    }

    fun deleteRule(id: Int?) {
        id?.let {
            viewModelScope.launch(Dispatchers.IO) {
                repository.deleteRule(it)
            }
        }
    }

    fun updateRule(rule: AutoReplyEntity) = repository.updateRule(rule)


}