package com.rkbapps.autoreply.models

data class Contact(
    val id: String,
    val name: String?,
    val phoneNumber: String,
    val photoUri: String? = null
)
