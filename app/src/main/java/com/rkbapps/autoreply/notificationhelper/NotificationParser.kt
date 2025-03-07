package com.rkbapps.autoreply.notificationhelper

import android.app.Notification
import android.service.notification.StatusBarNotification
import com.rkbapps.autoreply.models.NotificationData

object NotificationParser {

    fun parseNotification(notification: StatusBarNotification): NotificationData {
        val id = notification.id
        val title = notification.notification.extras.getString(Notification.EXTRA_TITLE)
        val text = notification.notification.extras.getString(Notification.EXTRA_TEXT)
        val packageName=notification.packageName
        val subText = notification.notification.extras.getString(Notification.EXTRA_SUB_TEXT)
        val bigText = notification.notification.extras.getString(Notification.EXTRA_BIG_TEXT)
        val infoText = notification.notification.extras.getString(Notification.EXTRA_INFO_TEXT)
        val summaryText =
            notification.notification.extras.getString(Notification.EXTRA_SUMMARY_TEXT)

        return NotificationData(
            id = id,
            title = title,
            text = text,
            packageName =packageName,
            subText = subText,
            bigText = bigText,
            infoText = infoText,
            summaryText = summaryText
        )
    }
}