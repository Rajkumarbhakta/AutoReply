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

enum class MatchingType(
    val value: String,
    val meaning: String
){
    CONTAINS(
        "Contains",
        "Message includes any of the keywords"
    ),
    EXACT(
        "Exact match",
        "Message matches exactly with the keywords"
    ),
    STARTS_WITH(
        "Starts with",
        "Message starts with the keywords"
    ),
}