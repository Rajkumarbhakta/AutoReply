package com.rkbapps.autoreply.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.rkbapps.autoreply.data.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager
): ViewModel() {

    val darkTheme = preferenceManager.isDarkThemeEnabledFlow.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        false
    )

    fun changeDarkThemeStatus(status:Boolean){
        viewModelScope.launch {
            preferenceManager.changeDarkThemeStatus(status)
        }
    }

}