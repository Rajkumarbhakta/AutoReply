package com.rkbapps.autoreply.ui.screens.addeditautoreply

import android.content.Context
import android.provider.ContactsContract
import com.rkbapps.autoreply.data.AutoReplyDao
import com.rkbapps.autoreply.data.AutoReplyEntity
import com.rkbapps.autoreply.models.Contact
import com.rkbapps.autoreply.utils.ChooseContactType
import com.rkbapps.autoreply.utils.UiState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddEditAutoReplyScreenRepository @Inject constructor(
    @ApplicationContext private val mApplication: Context,
    private val autoReplyDao: AutoReplyDao
) {

    private val allContacts = mutableListOf<Contact>()

    private val rule = MutableStateFlow(AutoReplyEntity(trigger = "", reply = "", name = ""))
    val ruleState = rule.asStateFlow()

    private val contacts = MutableStateFlow(emptyList<Contact>())
    val contactsState = contacts.asStateFlow()

    private val _ruleAddUpdateStatus = MutableStateFlow(UiState<AutoReplyEntity>())
    val ruleAddUpdateStatus = _ruleAddUpdateStatus.asStateFlow()

    suspend fun loadContacts() {
        if (allContacts.isNotEmpty()) {
            return // Contacts already loaded
        }
        val contactsList = getPhoneContacts()
        allContacts.clear()
        allContacts.addAll(contactsList)
    }

    suspend fun loadIncludeContacts() {
        val contactsList = allContacts
        val excludedContacts = rule.value.excludeContacts
        val filteredContacts = contactsList.filter { contact ->
            excludedContacts.none { it == contact }
        }
        contacts.emit(filteredContacts)
    }

    suspend fun loadExcludeContacts() {
        val contactsList = allContacts
        val includedContacts = rule.value.includeContacts
        val filteredContacts = contactsList.filter { contact ->
            includedContacts.none { it == contact }
        }
        contacts.emit(filteredContacts)
    }

    fun updateRule(rule: AutoReplyEntity) { this.rule.value = rule }

    suspend fun searchContacts(query: String,type: ChooseContactType) {
        val availableContacts = contacts.value
        if (query.isNotBlank() && query.isNotEmpty()){
            val filteredContacts = availableContacts.filter { contact ->
                contact.name?.contains(query, ignoreCase = true) == true || contact.phoneNumber.contains(query, ignoreCase = true)
            }
            contacts.value = filteredContacts
        }else{
            when (type) {
                ChooseContactType.INCLUDE -> loadIncludeContacts()
                ChooseContactType.EXCLUDE -> loadExcludeContacts()
            }
        }
    }

    suspend fun loadRule(id: Int) {
        val autoReplyEntity = autoReplyDao.getAutoReplyById(id)
        autoReplyEntity?.let {
            rule.emit(it)
        }
    }

    suspend fun deleteRule(id: Int) {
        try {
            autoReplyDao.deleteAutoReplyById(id)
        } catch (e: Exception) {
        }
    }

    suspend fun addNewAutoReply(
        autoReplyEntity: AutoReplyEntity,
        addEditType: AddEditType = AddEditType.ADD
    ) {
        _ruleAddUpdateStatus.emit(UiState(isLoading = true))
        try {
            if (autoReplyEntity.name.isBlank()) {
                _ruleAddUpdateStatus.emit(UiState(isError = true, message = "Rule name cannot be empty"))
                return
            }
            if (autoReplyEntity.reply.isBlank()) {
                _ruleAddUpdateStatus.emit(UiState(isError = true, message = "Send message cannot be empty"))
                return
            }
            if (autoReplyEntity.trigger.isBlank()) { _ruleAddUpdateStatus.emit(UiState(isError = true, message = "Receive message cannot be empty"))
                return
            }

            when(addEditType){
                AddEditType.ADD -> autoReplyDao.insertAutoReply(autoReplyEntity)
                AddEditType.EDIT -> autoReplyDao.updateAutoReply(autoReplyEntity)
            }
            _ruleAddUpdateStatus.emit(UiState(data = autoReplyEntity))

        } catch (e: Exception) {
            _ruleAddUpdateStatus.emit(UiState(isError = true, message = e.localizedMessage))
        }
    }

    private suspend fun getPhoneContacts(): ArrayList<Contact> = withContext(Dispatchers.IO) {
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
            val nameIndex =
                cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
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

        return@withContext contactsList
    }

}

enum class AddEditType {
    ADD, EDIT
}