package com.rkbapps.autoreply.ui.screens.addeditautoreply

import android.content.Context
import android.provider.ContactsContract
import com.rkbapps.autoreply.data.AutoReplyDao
import com.rkbapps.autoreply.data.AutoReplyEntity
import com.rkbapps.autoreply.data.MatchingType
import com.rkbapps.autoreply.models.Contact
import com.rkbapps.autoreply.utils.UiState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class AddEditAutoReplyScreenRepository @Inject constructor(
    @ApplicationContext private val mApplication: Context,
    private val autoReplyDao: AutoReplyDao
) {
    private val rule = MutableStateFlow(AutoReplyEntity(trigger = "", reply = "", matchingType = MatchingType.CONTAINS))
    val ruleState = rule.asStateFlow()


    private val contacts = MutableStateFlow(emptyList<Contact>())
    val contactsState = contacts.asStateFlow()

    suspend fun loadContacts() {
        contacts.emit(getPhoneContacts())
    }




    val matchingTypeList = MatchingType.entries.toList()

    private val _autoReplyAddStatus = MutableStateFlow(UiState<AutoReplyEntity>())
    val autoReplyAddStatus = _autoReplyAddStatus.asStateFlow()


    fun updateRule(rule: AutoReplyEntity) {
        this.rule.value = rule
    }

    suspend fun addNewAutoReply(autoReplyEntity: AutoReplyEntity,addEditType: AddEditType = AddEditType.ADD){
        _autoReplyAddStatus.emit(UiState(isLoading = true))
        try {
            if(autoReplyEntity.reply.isBlank()){
                _autoReplyAddStatus.emit(UiState(isError = true, message = "Send message cannot be empty"))
                return
            }
            if(autoReplyEntity.trigger.isBlank()){
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


    private suspend fun getPhoneContacts(): ArrayList<Contact> {
        val contactsList = ArrayList<Contact>()

        val contactsCursor = mApplication.contentResolver?.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI
            ),
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        contactsCursor?.use { cursor ->
            val idIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val photoIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)

            while (cursor.moveToNext()) {
                val id = cursor.getString(idIndex)
                val name = cursor.getString(nameIndex)
                val phoneNumber = cursor.getString(phoneIndex)
                val photoUri = cursor.getString(photoIndex)

                if (!name.isNullOrEmpty() && !phoneNumber.isNullOrEmpty()) {
                    contactsList.add(
                        Contact(
                            id = id,
                            name = name,
                            phoneNumber = phoneNumber,
                            photoUri = photoUri
                        )
                    )
                }
            }
        }

        return contactsList
    }


}

enum class AddEditType{
    ADD,EDIT
}