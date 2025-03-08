package com.rkbapps.autoreply.notificationhelper

import android.app.Notification
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.rkbapps.autoreply.services.RestartServiceJob
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    @ApplicationContext private val applicationContext: Context
) {

    companion object{
        private val whatsappPackageName = listOf("com.whatsapp.w4b", "com.whatsapp")
        private val handledNotifications = mutableSetOf<String>()
    }


    fun manageOnNotificationPosted(notificationService:NotificationListenerService,notification: StatusBarNotification){
        val data = NotificationParser.parseNotification(notification)
        if(whatsappPackageName.contains(data.packageName)){
            Log.d("NotificationListenerService", "onNotificationPosted : $data")
            val extras = notification.notification.extras
            val title = data.title?:"" // Sender name
            val text = data.text?:"" // Message content
            if (title.isNotBlank() && text.isNotBlank()) {
                Log.d("AutoReply", "New message from $title: $text")
                Log.d("AutoReply", "notification :::: ${notification.key}")
                if(handledNotifications.contains(notification.key)){
                    Log.d("NotificationListenerService","Notification already handled")
                    clickButton(notificationService,notification,"Mark as read")
                    handledNotifications.remove(notification.key)
                    return
                }
                // If it's a personal message (not a group), reply
                if (!text.contains(":")) {
                    Log.d("AutoReply", "Personal message detected. Auto-replying...")
                    if(text.startsWith("hello",ignoreCase = true)){
                        reply(notificationService,notification, "Hello! there")
                    }
                }
            }
        }
    }

    fun manageOnNotificationRemoved(notificationService:NotificationListenerService,notification: StatusBarNotification){
        val data = NotificationParser.parseNotification(notification)
        Log.d("NotificationListenerService", "onNotificationRemoved whatsapp : ${data}")
    }

    private fun clickButton(notificationService:NotificationListenerService,sbn: StatusBarNotification, button: String) {
        val click: Int? = NotificationUtils.getClickAction(sbn.notification, button)
        if (click != null) {
            Log.d("NotificationListenerService","Found $button action")
            sbn.notification.actions[click].actionIntent.send()
        }
        notificationService.cancelNotification(sbn.key)
    }

    private fun reply(notificationService:NotificationListenerService,sbn: StatusBarNotification, message: String) {
        val action: Action? = NotificationUtils.getQuickReplyAction(sbn.notification, applicationContext.packageName)
        if (action != null) {
            Log.d("NotificationListenerService","Found reply action")
            try {
                clickButton(notificationService,sbn,"Mark as read")
                action.sendReply(applicationContext, message)
                Log.d("NotificationListenerService","After send reply")
            } catch (e: PendingIntent.CanceledException) {
                Log.d("NotificationListenerService","CRAP $e")
            }
        } else {
            Log.d("NotificationListenerService","Reply action not found")
        }
        handledNotifications.add(sbn.key)
        notificationService.cancelNotification(sbn.key)
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

    private fun clearAllWhatsAppNotifications(notificationService:NotificationListenerService) {
        val activeNotifications = notificationService.activeNotifications
        for (sbn in activeNotifications) {
            if (sbn.packageName == "com.whatsapp") {
                notificationService.cancelNotification(sbn.key)
            }
        }
    }

}