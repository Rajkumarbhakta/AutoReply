package com.rkbapps.autoreply.services

import android.content.ComponentName
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.rkbapps.autoreply.notificationhelper.NotificationRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyNotificationListenerService @Inject constructor() : NotificationListenerService() {


    @Inject
    lateinit var repository: NotificationRepository

    override fun onDestroy() {
        super.onDestroy()
        repository.scheduleRestartJob(this)
        repository.onDestroy()
    }


    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d("NotificationListenerService", "onListenerConnected")
    }

    override fun onListenerDisconnected() {
        Log.d("NotificationListenerService", "Listener disconnected! Restarting...")
        requestRebind(ComponentName(this, MyNotificationListenerService::class.java))
    }


    override fun onNotificationPosted(newNotification: StatusBarNotification) {
        repository.manageOnNotificationPosted(this, newNotification)
    }


    override fun onNotificationRemoved(removedNotification: StatusBarNotification) {
        repository.manageOnNotificationRemoved(this, removedNotification)
    }

}