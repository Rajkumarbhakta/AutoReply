package com.rkbapps.autoreply.notificationhelper


import android.app.Notification
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.Locale

object NotificationUtils {

    private val REPLY_KEYWORDS = arrayOf("reply", "android.intent.extra.text")

    fun getQuickReplyAction(n: Notification, packageName: String): Action? {
        var action: NotificationCompat.Action? = null
        action = getQuickReplyAction(n)
        if (action == null)
            return null
        return Action(action, packageName, true)
    }

    fun getClickAction(n: Notification, text: String): Int? {
        for (i in 0 until NotificationCompat.getActionCount(n)) {
            val action = NotificationCompat.getAction(n, i)
            Log.d("NotificationUtils", "Action = ${action?.title}")
            if (action != null) {
                if (action.title.toString().equals(text, ignoreCase = true)) {
                    return i
                }
            }
        }
        return null
    }

    private fun getQuickReplyAction(n: Notification): NotificationCompat.Action? {

        Log.d("NotificationUtils", "Action count = ${NotificationCompat.getActionCount(n)}")
        for (i in 0 until NotificationCompat.getActionCount(n)) {
            val action = NotificationCompat.getAction(n, i)
            if (action != null) {
                Log.d("NotificationUtils", "Action = ${action.title}")
                if (action.remoteInputs != null) {
                    for (element in action.remoteInputs!!) {
                        Log.d("NotificationUtils", "action no:$i  ${element.resultKey}")
                    }
                }
            }
        }
        for (i in 0 until NotificationCompat.getActionCount(n)) {
            val action = NotificationCompat.getAction(n, i)
            if (action != null) {
                if (action.remoteInputs != null) {
                    for (element in action.remoteInputs!!) {
                        if (isKnownReplyKey(element.resultKey))
                            return action
                    }
                }
            }
        }
        return null
    }

    private fun isKnownReplyKey(resultKey: String): Boolean {
        if (TextUtils.isEmpty(resultKey))
            return false
        for (keyword in REPLY_KEYWORDS)
            if (resultKey.lowercase(Locale.ROOT).contains(keyword))
                return true
        return false
    }

}
