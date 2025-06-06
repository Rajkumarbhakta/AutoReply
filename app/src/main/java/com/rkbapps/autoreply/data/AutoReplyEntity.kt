package com.rkbapps.autoreply.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rkbapps.autoreply.utils.ReplyType
import kotlinx.coroutines.Delay

@Entity(tableName = "auto_reply")
data class AutoReplyEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val receive: String,
    val send: String,
    val matchingType: MatchingType,
    val delay: Long = 0L,
    val replyType: ReplyType = ReplyType.INDIVIDUAL,
    val includeContacts: List<String> = emptyList(),
    val excludeContacts: List<String> = emptyList(),
    val schedule: List<ReplySchedule> = emptyList(),
    val isActive: Boolean = true
)

data class ReplySchedule(
    val startTime: Long? = null,
    val endTime: Long? = null,
    val daysOfWeek: DaysOfWeek = DaysOfWeek.MONDAY,
)

enum class DaysOfWeek {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

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