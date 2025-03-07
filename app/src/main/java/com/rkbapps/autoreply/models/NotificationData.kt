package com.rkbapps.autoreply.models

data class NotificationData(
    val id: Int,
    val title: String?,
    val text: String?,
    val packageName: String?,
    val subText: String?,
    val bigText: String?,
    val infoText: String?,
    val summaryText: String?
)