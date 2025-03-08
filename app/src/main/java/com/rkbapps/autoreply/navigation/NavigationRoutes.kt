package com.rkbapps.autoreply.navigation

import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
data class AddEditAutoReply(val data:String?=null)