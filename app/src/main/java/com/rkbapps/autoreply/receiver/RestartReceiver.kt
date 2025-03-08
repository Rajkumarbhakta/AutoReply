package com.rkbapps.autoreply.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.rkbapps.autoreply.services.KeepAliveService
import com.rkbapps.autoreply.services.MyNotificationListenerService

class RestartReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("RestartReceiver", "Restarting NotificationListenerService...")
        context?.startService(Intent(context, KeepAliveService::class.java))
    }
}
