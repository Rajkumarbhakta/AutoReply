package com.rkbapps.autoreply.navigation

import kotlinx.serialization.Serializable

sealed class NavigationRoutes{
    @Serializable
    object Home: NavigationRoutes()
    @Serializable
    object AutoReply: NavigationRoutes()
    @Serializable
    object Sent: NavigationRoutes()
    @Serializable
    object Settings: NavigationRoutes()

    @Serializable
    data class AddEditAutoReply(val data:String?=null): NavigationRoutes()
}