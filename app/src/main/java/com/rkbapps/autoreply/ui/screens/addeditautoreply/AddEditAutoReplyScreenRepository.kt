package com.rkbapps.autoreply.ui.screens.addeditautoreply

import com.rkbapps.autoreply.data.AutoReplyDao
import com.rkbapps.autoreply.data.AutoReplyEntity
import com.rkbapps.autoreply.data.MatchingType
import com.rkbapps.autoreply.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class AddEditAutoReplyScreenRepository @Inject constructor(
    private val autoReplyDao: AutoReplyDao
) {

    val matchingTypeList = MatchingType.entries.toList()

    private val _autoReplyAddStatus = MutableStateFlow(UiState<AutoReplyEntity>())
    val autoReplyAddStatus = _autoReplyAddStatus.asStateFlow()


    suspend fun addNewAutoReply(autoReplyEntity: AutoReplyEntity,addEditType: AddEditType = AddEditType.ADD){
        _autoReplyAddStatus.emit(UiState(isLoading = true))
        try {
            if(autoReplyEntity.send.isBlank()){
                _autoReplyAddStatus.emit(UiState(isError = true, message = "Send message cannot be empty"))
                return
            }
            if(autoReplyEntity.receive.isBlank()){
                _autoReplyAddStatus.emit(UiState(isError = true, message = "Receive message cannot be empty"))
                return
            }
            if(addEditType== AddEditType.ADD){
                autoReplyDao.insertAutoReply(autoReplyEntity)
            }else{
                autoReplyDao.updateAutoReply(autoReplyEntity)
            }
            _autoReplyAddStatus.emit(UiState(data = autoReplyEntity))

        }catch (e: Exception){
            _autoReplyAddStatus.emit(UiState(isError = true, message = e.localizedMessage))
        }
    }

}

enum class AddEditType{
    ADD,EDIT
}