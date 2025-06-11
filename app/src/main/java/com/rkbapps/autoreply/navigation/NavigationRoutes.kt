package com.rkbapps.autoreply.navigation

import androidx.appcompat.widget.DialogTitle
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
    data class AddEditAutoReply(val id: Int?=null): NavigationRoutes()
    @Serializable
    data object AddEdit: NavigationRoutes()
    @Serializable
    data object ChooseContact: NavigationRoutes()
    @Serializable
    data object ManageSchedule: NavigationRoutes()

    @Serializable
    data object HelpCenter: NavigationRoutes()

    @Serializable
    data class ShowHtmlText(
        val title: String,
        val htmlText: String
    ): NavigationRoutes()

}