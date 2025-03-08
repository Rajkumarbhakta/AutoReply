package com.rkbapps.autoreply.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "auto_reply")
data class AutoReplyEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    val receive: String,
    val send: String,
    val matchingType: MatchingType,
    val isActive: Boolean = true
)

enum class MatchingType{
    CONTAINS,
    EXACT,
    STARTS_WITH,
}