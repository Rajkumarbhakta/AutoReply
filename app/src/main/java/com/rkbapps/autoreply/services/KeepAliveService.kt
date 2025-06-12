package com.rkbapps.autoreply.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.app.ServiceCompat.stopForeground
import com.rkbapps.autoreply.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class KeepAliveService : Service() {

    companion object {
        private const val NOTIFICATION_ID = 101
        private const val CHANNEL_ID = "keep_alive_channel"
        private val _isRunning = MutableStateFlow(false)
        val isRunning = _isRunning.asStateFlow()
    }


    override fun onCreate() {
        super.onCreate()
        Log.d("KeepAliveService", "Service Created")
        startForegroundService()
        _isRunning.value = true
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("KeepAliveService", "Service Started")
        if (intent?.action == "STOP_SERVICE") {
            stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
            stopSelf() // Stops the service
            return START_NOT_STICKY
        }

        // Start the service in foreground mode
        startForeground(NOTIFICATION_ID, createNotification())
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("KeepAliveService", "Service Destroyed,")
        _isRunning.value = false
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("ForegroundServiceType")
    private fun startForegroundService() {
        val notification = createNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
            startForeground(NOTIFICATION_ID, notification)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }


    private fun createNotification(): Notification {
        val channelId = CHANNEL_ID
        val notificationManager = getSystemService(NotificationManager::class.java)

        // Create a Notification Channel for Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Keep Alive Service",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager?.createNotificationChannel(channel)
        }

        // Intent to stop the service
        val stopIntent = Intent(this, KeepAliveService::class.java).apply {
            action = "STOP_SERVICE"
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Auto Reply is running")
            .setContentText("The service is running to ensure auto-reply functionality remains active.")
            .setSmallIcon(R.drawable.notification_icon)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(android.R.drawable.ic_delete, "Stop", stopPendingIntent) // Stop action
            .setAutoCancel(false) // Allow dismissal
            .build()
    }


    private fun restartService() {
        val intent = Intent(applicationContext, KeepAliveService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            applicationContext.startForegroundService(intent)
        } else {
            applicationContext.startService(intent)
        }
    }
}