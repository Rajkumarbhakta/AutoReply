package com.rkbapps.autoreply.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rkbapps.autoreply.utils.ReplyType
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
    companion object{
        val IS_AUTO_REPLY_ENABLED = booleanPreferencesKey("is_auto_reply_enabled")
        val IS_SMART_REPLY_ENABLED = booleanPreferencesKey("is_smart_reply_enabled")
        val REPLY_TYPE = stringPreferencesKey("reply_type")
    }

    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = context.packageName)

    val isAutoReplyEnableFlow:Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map {
        it[IS_AUTO_REPLY_ENABLED] == true
    }
    val isSmartReplyEnableFlow:Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map {
        it[IS_SMART_REPLY_ENABLED] ?: false
    }

    val replyTypeFlow:Flow<String> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map {
        it[REPLY_TYPE]?: ReplyType.INDIVIDUAL
    }


    suspend fun changeAutoReplyStatus(status:Boolean){
        context.dataStore.edit {
            it[IS_AUTO_REPLY_ENABLED] = status
        }
    }

    suspend fun changeSmartReplyStatus(status:Boolean){
        context.dataStore.edit {
            it[IS_SMART_REPLY_ENABLED] = status
        }
    }

    suspend fun changeReplyType(type:String){
        context.dataStore.edit {
            it[REPLY_TYPE] = type
        }
    }


}