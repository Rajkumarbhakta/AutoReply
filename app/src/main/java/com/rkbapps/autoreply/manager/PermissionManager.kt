package com.rkbapps.autoreply.manager

interface PermissionManager {

    fun isNotificationPermissionGranted(): Boolean
    fun isBatteryRestricted(): Boolean
    fun requestNotificationPermission()
    fun requestBatteryPermissions()

}