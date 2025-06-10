package com.rkbapps.autoreply.notificationhelper

import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.icu.util.Calendar
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.rkbapps.autoreply.data.AutoReplyDao
import com.rkbapps.autoreply.data.AutoReplyEntity
import com.rkbapps.autoreply.data.PreferenceManager
import com.rkbapps.autoreply.data.Time
import com.rkbapps.autoreply.services.KeepAliveService
import com.rkbapps.autoreply.services.RestartServiceJob
import com.rkbapps.autoreply.utils.ReplyType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val dataBase: AutoReplyDao,
    private val databaseReplyManager: DatabaseReplyManager,
    private val smartReplyManager: SmartReplyManager,
    private val preferenceManager: PreferenceManager
) {

    private var autoReplyList = listOf<AutoReplyEntity>()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        getAllAutoReply()
    }

    fun getAllAutoReply() {
        try {
            coroutineScope.launch {
                val data = dataBase.getAllActiveAutoRepliesOnce()
                autoReplyList = data
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private val whatsappPackageName = listOf("com.whatsapp.w4b", "com.whatsapp")
        private val handledNotifications = mutableSetOf<String>()
    }

    var isSmartReplyEnabled = false
        private set

    init {
        coroutineScope.launch {
            preferenceManager.isSmartReplyEnableFlow.collect {
                isSmartReplyEnabled = it
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
            val title = data.title // Sender name
            val text = data.text // Message content
            if (!title.isNullOrBlank() && !text.isNullOrBlank()) {
                Log.d("AutoReply", "New message from $title: $text")
                Log.d("AutoReply", "notification :::: ${notification.key}")
                if (handledNotifications.contains(notification.key)) {
                    Log.d("NotificationListenerService", "Notification already handled")
                    clickButton(notificationService, notification, "Mark as read")
                    handledNotifications.remove(notification.key)
                    return
                }
                // If it's a personal message (not a group), reply
                val txt = text.replace(":", "")
                val rule = findRule(autoReplyList, txt)
                if (rule!=null){
                    val replyMessage = getReplyMessage(message = text, rule = rule)
                    replyMessage?.let {
                        reply(notificationService, notification, replyMessage)
                    }
                }
            }
        }
    }


    fun getReplyMessage(message: String,rule: AutoReplyEntity): String? {
        return databaseReplyManager.generateReply(message,rule)
    }

    fun findRule(rules: List<AutoReplyEntity>,txt: String): AutoReplyEntity? {
        val data = rules.find { txt == it.trigger } ?: rules.find { txt.startsWith(it.trigger, ignoreCase = true) }
        return data ?: rules.find { txt.contains(it.trigger, ignoreCase = true) }
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