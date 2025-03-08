package com.rkbapps.autoreply.services

import android.app.Notification
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.content.ContextCompat
import com.rkbapps.autoreply.notificationhelper.Action
import com.rkbapps.autoreply.notificationhelper.NotificationParser
import com.rkbapps.autoreply.notificationhelper.NotificationUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyNotificationListenerService: NotificationListenerService()  {

    private lateinit var context :Context
    private val handledNotifications = mutableSetOf<String>()

    override fun onCreate() {
        super.onCreate()
        // Register broadcast from UI
        context = applicationContext
    }

    override fun onDestroy() {
        super.onDestroy()
        scheduleRestartJob(this)
    }


    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d("NotificationListenerService","onListenerConnected")
    }

    override fun onListenerDisconnected() {
        Log.d("NotificationListenerService", "Listener disconnected! Restarting...")
        requestRebind(ComponentName(this, MyNotificationListenerService::class.java))
    }


    override fun onNotificationPosted(newNotification: StatusBarNotification?) {
        newNotification?.let { notification ->
            val data = NotificationParser.parseNotification(newNotification)
            if(data.packageName == "com.whatsapp.w4b"|| data.packageName == "com.whatsapp"){
                Log.d("NotificationListenerService", "onNotificationPosted whatsapp : ${data}")
                val extras = notification.notification.extras
                val title = extras.getString(Notification.EXTRA_TITLE) // Sender name
                val text = extras.getCharSequence(Notification.EXTRA_TEXT)?:"" // Message content
                if (title != null && text != null) {
                    Log.d("AutoReply", "New message from $title: $text")
                    Log.d("AutoReply", "notification :::: ${notification.key}")
                    if(handledNotifications.contains(notification.key)){
                        Log.d("NotificationListenerService","Notification already handled")
                        clickButton(notification,"Mark as read")
                        handledNotifications.remove(notification.key)
                        return
                    }
                    // If it's a personal message (not a group), reply
                    if (!text.contains(":")) {
                        Log.d("AutoReply", "Personal message detected. Auto-replying...")
                        if(text.startsWith("hello",ignoreCase = true)){
                            reply(notification, "Hello! there")
                        }
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


    fun scheduleRestartJob(context: Context) {
        val componentName = ComponentName(context, RestartServiceJob::class.java)
        val jobInfo = JobInfo.Builder(123, componentName)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setPersisted(true)  // Keeps job even after reboot
            .setMinimumLatency(5000)  // Wait 5 sec before restarting
            .build()

        val jobScheduler = context.getSystemService(JobScheduler::class.java)
        jobScheduler.schedule(jobInfo)
    }



    private fun clearAllWhatsAppNotifications() {
        val activeNotifications = activeNotifications
        for (sbn in activeNotifications) {
            if (sbn.packageName == "com.whatsapp") {
                cancelNotification(sbn.key)
            }
        }
    }

}