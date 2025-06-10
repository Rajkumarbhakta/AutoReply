package com.rkbapps.autoreply.ui.screens.rules

import com.rkbapps.autoreply.data.AutoReplyDao
import com.rkbapps.autoreply.data.AutoReplyEntity
import javax.inject.Inject

class AutoReplyRulesRepository @Inject constructor(
    private val autoReplyDao: AutoReplyDao
) {

    val autoReplyRules = autoReplyDao.getAllAutoReplies()


    suspend fun updateRule(autoReply: AutoReplyEntity) {
        autoReplyDao.updateAutoReply(autoReply)
    }


}