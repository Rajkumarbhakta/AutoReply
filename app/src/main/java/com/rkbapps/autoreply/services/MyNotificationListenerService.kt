package com.rkbapps.autoreply.services

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.rkbapps.autoreply.notificationhelper.Action
import com.rkbapps.autoreply.notificationhelper.NotificationParser
import com.rkbapps.autoreply.notificationhelper.NotificationUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyNotificationListenerService: NotificationListenerService()  {

    private var commandFromUIReceiver: CommandFromUIReceiver? = null
    private lateinit var context :Context
    private val handledNotifications = mutableSetOf<String>()

    override fun onCreate() {
        super.onCreate()
        // Register broadcast from UI
        context = applicationContext
        commandFromUIReceiver = CommandFromUIReceiver()
        val filter = IntentFilter()
        filter.addAction(READ_COMMAND_ACTION)
        //ContextCompat.registerReceiver(this, commandFromUIReceiver, filter, ContextCompat.RECEIVER_EXPORTED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(commandFromUIReceiver, filter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(commandFromUIReceiver, filter)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (commandFromUIReceiver != null) {
            unregisterReceiver(commandFromUIReceiver)
            Log.d("NotificationListenerService", "BroadcastReceiver unregistered")
        }
    }


    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d("NotificationListenerService","onListenerConnected")
    }


    override fun onNotificationPosted(newNotification: StatusBarNotification?) {
        newNotification?.let { notification ->
            val data = NotificationParser.parseNotification(newNotification)
            if(data.packageName == "com.whatsapp.w4b"|| data.packageName == "com.whatsapp"){
                Log.d("NotificationListenerService", "onNotificationPosted whatsapp : ${data}")
                val extras = notification.notification.extras
                val title = extras.getString(Notification.EXTRA_TITLE) // Sender name
                val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() // Message content
                if (title != null && text != null) {
                    Log.d("AutoReply", "New message from $title: $text")
                    Log.d("AutoReply", "notification :::: ${notification.key}")
                    if(NotificationUtils.getClickAction(notification.notification, "Mark as read")==null){
                        Log.d("NotificationListenerService","Mark as read action not found")
                        this.cancelNotification(notification.key)
                        return
                    }
                    if(handledNotifications.contains(notification.key)){
                        Log.d("NotificationListenerService","Notification already handled")
                        clickButton(notification,"Mark as read")
                        handledNotifications.remove(notification.key)
                        return
                    }
                    // If it's a personal message (not a group), reply
                    if (!text.contains(":")) {
                        Log.d("AutoReply", "Personal message detected. Auto-replying...")
                        reply(notification, "Hello")
                    }
                }
            }
        }
    }


    override fun onNotificationRemoved(removedNotification: StatusBarNotification) {
        val data = NotificationParser.parseNotification(removedNotification)
        Log.d("NotificationListenerService", "onNotificationRemoved whatsapp : ${data}")
    }


    private fun clickButton(sbn: StatusBarNotification, button: String) {
        val click: Int? = NotificationUtils.getClickAction(sbn.notification, button)
        if (click != null) {
            Log.d("NotificationListenerService","Found $button action")
            sbn.notification.actions[click].actionIntent.send()
        }
        this.cancelNotification(sbn.key)
    }

    private fun reply(sbn: StatusBarNotification, message: String) {
        val action: Action? = NotificationUtils.getQuickReplyAction(sbn.notification, packageName)
        if (action != null) {
            Log.d("NotificationListenerService","Found reply action")
            try {
                clickButton(sbn,"Mark as read")
                action.sendReply(applicationContext, message)
                Log.d("NotificationListenerService","After send reply")
            } catch (e: PendingIntent.CanceledException) {
                Log.d("NotificationListenerService","CRAP $e")
            }
        } else {
            Log.d("NotificationListenerService","Reply action not found")
        }
        handledNotifications.add(sbn.key)
        cancelNotification(sbn.key)
    }


    private fun clearAllWhatsAppNotifications() {
        val activeNotifications = activeNotifications
        for (sbn in activeNotifications) {
            if (sbn.packageName == "com.whatsapp") {
                cancelNotification(sbn.key)
            }
        }
    }

    internal inner class CommandFromUIReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.getStringExtra(COMMAND_KEY) == CLEAR_NOTIFICATIONS)
            // remove Notifications
                cancelAllNotifications()
            else if (intent.getStringExtra(COMMAND_KEY) == GET_ACTIVE_NOTIFICATIONS)
            // Read Notifications
                fetchCurrentNotifications()
        }

//        override fun onReceive(context: Context, intent: Intent) {
//            when (intent.getStringExtra(COMMAND_KEY)) {
//                CLEAR_NOTIFICATIONS -> {
//                    Log.d("CommandFromUIReceiver", "Clearing all notifications")
//                    cancelAllNotifications()
//                }
//                GET_ACTIVE_NOTIFICATIONS -> {
//                    Log.d("CommandFromUIReceiver", "Fetching active notifications")
//                    fetchCurrentNotifications()
//                }
//            }
//        }
    }

    private fun fetchCurrentNotifications() {
        val activeNotificationCount = this@MyNotificationListenerService.activeNotifications.size

        if (activeNotificationCount > 0) {
            for (count in 0 until activeNotificationCount) {
                val sbn = this@MyNotificationListenerService.activeNotifications[count]
//                sendResultOnUI("#" + count.toString() + " Package: " + sbn.packageName + "\n")
            }
        } else {
//            sendResultOnUI("No active Notification found")
        }
//        sendResultOnUI("===== Notification List END====")
    }

    companion object {
        //Update UI action
        const val UPDATE_UI_ACTION = "ACTION_UPDATE_UI"
        const val READ_COMMAND_ACTION = "ACTION_READ_COMMAND"

        // Bundle Key Value Pair
        const val RESULT_KEY = "readResultKey"
        const val RESULT_VALUE = "readResultValue"

        //Actions sent from UI
        const val COMMAND_KEY = "READ_COMMAND"
        const val CLEAR_NOTIFICATIONS = "clearAll"
        const val GET_ACTIVE_NOTIFICATIONS = "list"
    }
}