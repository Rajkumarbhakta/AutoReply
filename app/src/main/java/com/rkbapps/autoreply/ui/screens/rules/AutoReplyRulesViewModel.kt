package com.rkbapps.autoreply.ui.screens.rules

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkbapps.autoreply.data.AutoReplyEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AutoReplyRulesViewModel @Inject constructor(
    private val repository: AutoReplyRulesRepository,
): ViewModel() {

    val autoReplyRules = repository.autoReplyRules.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    fun updateRule(autoReply: AutoReplyEntity){
        viewModelScope.launch {
            repository.updateRule(autoReply)
        }
    }

}