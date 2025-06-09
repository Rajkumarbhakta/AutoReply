package com.rkbapps.autoreply.notificationhelper

import android.util.Log
import com.google.mlkit.nl.smartreply.SmartReply
import com.google.mlkit.nl.smartreply.SmartReplySuggestionResult
import com.google.mlkit.nl.smartreply.TextMessage
import com.rkbapps.autoreply.data.AutoReplyDao
import com.rkbapps.autoreply.data.AutoReplyEntity
import com.rkbapps.autoreply.data.MatchingType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

interface ReplyManager {
    fun generateReply(message: String): String
}

open class GenericReplyManager() : ReplyManager {

    companion object {
        const val NO_REPLY = "Sorry I don't understand";
    }

    override fun generateReply(message: String): String {
        if (message.equals("hello", ignoreCase = true)) {
            return "Hi! how can i help you?"
        }
        if (message.equals("hi", ignoreCase = true)) {
            return "Hi! how can i help you?"
        }
        if (message.equals("how are you", ignoreCase = true)) {
            return "I am fine, thank you!"
        }
        return NO_REPLY
    }
}

open class DatabaseReplyManager @Inject constructor(
    private val dataBase: AutoReplyDao
) : GenericReplyManager() {

    private var autoReplyList = listOf<AutoReplyEntity>()

    init {
        getAllAutoReply()
    }

    fun getAllAutoReply() {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                dataBase.getAllAutoReplies().collect {
                    autoReplyList = it
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun generateReply(message: String): String {
        autoReplyList.find {
            message.contains(it.receive, ignoreCase = true)
        }?.let {
            when (it.matchingType) {
                MatchingType.EXACT -> {
                    if (message.equals(it.receive, ignoreCase = true)) {
                        return it.send
                    }
                }

                MatchingType.STARTS_WITH -> {
                    if (message.startsWith(it.receive, ignoreCase = true)) {
                        return it.send
                    }
                }

                MatchingType.CONTAINS -> {
                    if (message.contains(it.receive, ignoreCase = true)) {
                        return it.send
                    }
                }
            }
        }
        return super.generateReply(message)
    }

}

class SmartReplyManager @Inject constructor(
    private val dataBase: AutoReplyDao
) : DatabaseReplyManager(dataBase) {
    val smartReplyGenerator = SmartReply.getClient()
    fun generateSmartReply(message: String): String? {
        val chatHistory = ArrayList<TextMessage>()
        chatHistory.add(
            TextMessage.createForRemoteUser(
                message,
                System.currentTimeMillis(),
                UUID.randomUUID().toString()
            )
        )
        var reply: String? = null
        smartReplyGenerator.suggestReplies(chatHistory).continueWith { task ->
            when (task.result.status) {
                SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE -> {
                    Log.d("SmartReplyManager", "Not supported language")
                    reply = super.generateReply(message)
                }

                SmartReplySuggestionResult.STATUS_NO_REPLY -> {
                    Log.d("SmartReplyManager", "No reply")
                    reply = super.generateReply(message)
                }

                SmartReplySuggestionResult.STATUS_SUCCESS -> {
                    Log.d("SmartReplyManager", "Success ${task.result.suggestions.first().text}")
                    reply = task.result.suggestions.first().text
                }
            }
        }.addOnFailureListener {
            Log.d("SmartReplyManager", "Error ${it.message}")
            reply = super.generateReply(message)
        }
        return reply
    }

    override fun generateReply(message: String): String {
        return generateSmartReply(message) ?: super.generateReply(message)
    }
}