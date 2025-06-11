package com.rkbapps.autoreply.ui.screens.rules

import com.rkbapps.autoreply.data.AutoReplyDao
import com.rkbapps.autoreply.data.AutoReplyEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class AutoReplyRulesRepository @Inject constructor(
    private val autoReplyDao: AutoReplyDao
) {

    val query = MutableStateFlow("")


    @OptIn(ExperimentalCoroutinesApi::class)
    val autoReplyRules = autoReplyDao.getAllAutoReplies().combine(query) { rules, query ->
        if (query.isEmpty() || query.isBlank()) rules
        else rules.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.trigger.contains(query, ignoreCase = true) ||
                    it.reply.contains(query, ignoreCase = true)
        }
    }


    suspend fun updateRule(autoReply: AutoReplyEntity) {
        autoReplyDao.updateAutoReply(autoReply)
    }

    fun updateQuery(query: String) {
        this.query.value = query
    }


}