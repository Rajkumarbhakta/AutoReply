package com.rkbapps.autoreply.notificationhelper

import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.rkbapps.autoreply.data.PreferenceManager
import com.rkbapps.autoreply.services.KeepAliveService
import com.rkbapps.autoreply.services.RestartServiceJob
import com.rkbapps.autoreply.utils.ReplyType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val databaseReplyManager: DatabaseReplyManager,
    private val smartReplyManager: SmartReplyManager,
    private val preferenceManager: PreferenceManager
) {

    companion object {
        private val whatsappPackageName = listOf("com.whatsapp.w4b", "com.whatsapp")
        private val handledNotifications = mutableSetOf<String>()
    }

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    var isSmartReplyEnabled = false
        private set

    var replyType = ReplyType.INDIVIDUAL
        private set

    init {
//        coroutineScope.launch {
//            preferenceManager.isAutoReplyEnableFlow.collect {
//                isAutoReplyEnabled = it
//            }
//        }
        coroutineScope.launch {
            preferenceManager.isSmartReplyEnableFlow.collect {
                isSmartReplyEnabled = it
            }
        }
        coroutineScope.launch {
            preferenceManager.replyTypeFlow.collect {
                replyType = it
            }
        }
    }


    fun onDestroy() {
        coroutineScope.cancel() // Clean up when the class is no longer needed
    }


    fun manageOnNotificationPosted(
        notificationService: NotificationListenerService,
        notification: StatusBarNotification
    ) {
        val data = NotificationParser.parseNotification(notification)
        if (whatsappPackageName.contains(data.packageName) && KeepAliveService.isRunning.value) {
            Log.d("NotificationListenerService", "onNotificationPosted : $data")
            val extras = notification.notification.extras
            val title = data.title ?: "" // Sender name
            val text = data.text ?: "" // Message content
            if (title.isNotBlank() && text.isNotBlank()) {
                Log.d("AutoReply", "New message from $title: $text")
                Log.d("AutoReply", "notification :::: ${notification.key}")
                if (handledNotifications.contains(notification.key)) {
                    Log.d("NotificationListenerService", "Notification already handled")
                    clickButton(notificationService, notification, "Mark as read")
                    handledNotifications.remove(notification.key)
                    return
                }
                // If it's a personal message (not a group), reply
                if (replyType == ReplyType.INDIVIDUAL && !text.contains(":")) {
                    Log.d("AutoReply", "Personal message detected. Auto-replying...")
                    val replyMessage = getReplyMessage(message = text)
                    replyMessage?.let {
                        reply(notificationService, notification, replyMessage)
                    }
                } else if (replyType == ReplyType.GROUP && text.contains(":")) {
                    Log.d("AutoReply", "Group message detected. Auto-replying...")
                    val replyMessage = getReplyMessage(message = text)
                    replyMessage?.let {
                        reply(notificationService, notification, replyMessage)
                    }
                } else {
                    Log.d("AutoReply", "Group message detected. Auto-replying...")
                    val replyMessage = getReplyMessage(message = text)
                    replyMessage?.let {
                        reply(notificationService, notification, replyMessage)
                    }
                }

            }
        }
    }


    fun getReplyMessage(message: String): String? {
        val reply = databaseReplyManager.generateReply(message)
        Log.d("NotificationListenerService", "Smart reply : $reply")
        return if (reply == GenericReplyManager.NO_REPLY) null else reply
    }


    fun manageOnNotificationRemoved(
        notificationService: NotificationListenerService,
        notification: StatusBarNotification
    ) {
        val data = NotificationParser.parseNotification(notification)
        Log.d("NotificationListenerService", "onNotificationRemoved whatsapp : ${data}")
    }

    private fun clickButton(
        notificationService: NotificationListenerService,
        sbn: StatusBarNotification,
        button: String
    ) {
        val click: Int? = NotificationUtils.getClickAction(sbn.notification, button)
        if (click != null) {
            Log.d("NotificationListenerService", "Found $button action")
            sbn.notification.actions[click].actionIntent.send()
        }
        notificationService.cancelNotification(sbn.key)
    }

    private fun reply(
        notificationService: NotificationListenerService,
        sbn: StatusBarNotification,
        message: String
    ) {
        val action: Action? =
            NotificationUtils.getQuickReplyAction(sbn.notification, applicationContext.packageName)
        if (action != null) {
            Log.d("NotificationListenerService", "Found reply action")
            try {
                clickButton(notificationService, sbn, "Mark as read")
                action.sendReply(applicationContext, message)
                Log.d("NotificationListenerService", "After send reply")
            } catch (e: PendingIntent.CanceledException) {
                Log.d("NotificationListenerService", "CRAP $e")
            }
        } else {
            Log.d("NotificationListenerService", "Reply action not found")
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

    private fun clearAllWhatsAppNotifications(notificationService: NotificationListenerService) {
        val activeNotifications = notificationService.activeNotifications
        for (sbn in activeNotifications) {
            if (sbn.packageName == "com.whatsapp") {
                notificationService.cancelNotification(sbn.key)
            }
        }
    }

}