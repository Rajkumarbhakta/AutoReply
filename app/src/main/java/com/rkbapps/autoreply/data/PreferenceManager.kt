package com.rkbapps.autoreply.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = context.packageName)

    companion object {
        val IS_DARK_THEME_ENABLED = booleanPreferencesKey("is_dark_theme_enabled")
    }

    val isDarkThemeEnabledFlow: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map {
            it[IS_DARK_THEME_ENABLED] == true
        }

    suspend fun changeDarkThemeStatus(status: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_DARK_THEME_ENABLED] = status
        }
    }

}