package com.rkbapps.autoreply.screens.addeditautoreply

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.google.gson.Gson
import com.rkbapps.autoreply.data.AutoReplyEntity
import com.rkbapps.autoreply.navigation.AddEditAutoReply
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditAutoReplyScreenViewModel @Inject constructor(
    private val repository: AddEditAutoReplyScreenRepository,
    saveStateHandle: SavedStateHandle,
    private val gson: Gson
): ViewModel() {

    val matchingTypeList = repository.matchingTypeList

    val autoReplyAddStatus = repository.autoReplyAddStatus

    val data = saveStateHandle.toRoute<AddEditAutoReply>()
    val autoReplyObject = if(data.data != null){
            gson.fromJson(data.data, AutoReplyEntity::class.java)
        }else{
            null
        }

    fun addNewAutoReply(autoReplyEntity: AutoReplyEntity,addEditType: AddEditType = AddEditType.ADD){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addNewAutoReply(autoReplyEntity,addEditType)
        }
    }

    companion object{
        const val MAX_CHARACTER_LIMIT = 160
    }


}