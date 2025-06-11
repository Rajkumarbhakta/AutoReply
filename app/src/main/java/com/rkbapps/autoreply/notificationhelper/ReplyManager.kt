package com.rkbapps.autoreply.notificationhelper

import android.icu.util.Calendar
import android.util.Log
import com.google.mlkit.nl.smartreply.SmartReply
import com.google.mlkit.nl.smartreply.SmartReplySuggestionResult
import com.google.mlkit.nl.smartreply.TextMessage
import com.rkbapps.autoreply.data.AutoReplyDao
import com.rkbapps.autoreply.data.AutoReplyEntity
import com.rkbapps.autoreply.data.DaysOfWeek
import com.rkbapps.autoreply.data.MatchingType
import com.rkbapps.autoreply.data.Time
import com.rkbapps.autoreply.utils.ReplyType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

interface ReplyManager {
    fun generateReply(trigger: String,isGroupMessage:Boolean,rule: AutoReplyEntity): String?
}

open class DatabaseReplyManager @Inject constructor() : ReplyManager {

    fun getCurrentTime(): Time{
        val calendar = Calendar.getInstance()
        val currentHour: Int = calendar.get(Calendar.HOUR_OF_DAY) // 24-hour format
        val currentMinute: Int = calendar.get(Calendar.MINUTE)
        return Time(
            hour = currentHour,
            minute = currentMinute
        )
    }
    fun getCurrentDayOfWeekCalendar(): DaysOfWeek {
        val calendar = Calendar.getInstance()
        val dayOfWeekInt = calendar.get(Calendar.DAY_OF_WEEK) // SUNDAY is 1, MONDAY is 2, ..., SATURDAY is 7
        return DaysOfWeek.fromCalendarDay(dayOfWeekInt)
    }
    fun isCurrentTimeBetween(currentTime: Time, startTime: Time, endTime: Time): Boolean {
        val currentTimeInMinutes = currentTime.toMinutes()
        val startTimeInMinutes = startTime.toMinutes()
        val endTimeInMinutes = endTime.toMinutes()

        // Handle cases where the time range spans across midnight (e.g., 10 PM to 2 AM)
        return if (startTimeInMinutes <= endTimeInMinutes) {
            // Normal case: Start time is before or same as end time
            currentTimeInMinutes >= startTimeInMinutes && currentTimeInMinutes <= endTimeInMinutes
        } else {
            // Time range crosses midnight (e.g., start 22:00, end 02:00)
            // Current time must be after start time OR before end time
            currentTimeInMinutes >= startTimeInMinutes || currentTimeInMinutes <= endTimeInMinutes
        }
    }

    override fun generateReply(
        trigger: String,
        isGroupMessage: Boolean,
        rule: AutoReplyEntity
    ): String? {
        val isMatchTargetAudience = isMatchingTargetAudience(rule.replyType, isGroupMessage)
        if (isMatchTargetAudience) {
            if (rule.schedule == null) {
                // If no schedule is set, we can generate a reply immediately
                return getReplyMessage(rule.matchingType, rule.reply, trigger)
            } else {
                val currentTime = getCurrentTime()
                val currentDay = getCurrentDayOfWeekCalendar()
                if (rule.schedule.startTime != null && rule.schedule.endTime != null) {
                    val startTime = rule.schedule.startTime
                    val endTime = rule.schedule.endTime
                    val isValidTime = isCurrentTimeBetween(currentTime, startTime, endTime)
                    if (isValidTime && rule.schedule.daysOfWeek.contains(currentDay)) {
                        return getReplyMessage(rule.matchingType, rule.reply, trigger)
                    }
                }
            }
        }
        return null
    }

    private fun isMatchingTargetAudience(targetAudience: ReplyType, isGroupMessage: Boolean): Boolean {
        return when (targetAudience) {
            ReplyType.INDIVIDUAL -> !isGroupMessage
            ReplyType.GROUP -> isGroupMessage
            ReplyType.BOTH -> true
        }

    }

    private fun getReplyMessage(
        matchingType: MatchingType,
        reply: String,
        trigger: String
    ): String? = when (matchingType) {
        MatchingType.CONTAINS -> {
            if (trigger.contains(trigger, ignoreCase = true)) {
                reply
            } else {
                null
            }
        }

        MatchingType.EXACT -> {
            if (trigger.equals(trigger, ignoreCase = true)) {
                reply
            } else {
                null
            }
        }

        MatchingType.STARTS_WITH -> {
            if (trigger.startsWith(trigger, ignoreCase = true)) {
                reply
            } else {
                null
            }
        }
    }

}

class SmartReplyManager @Inject constructor() : ReplyManager {
    val smartReplyGenerator = SmartReply.getClient()
    fun generateSmartReply(message: String): String? {
        val chatHistory = ArrayList<TextMessage>()
        chatHistory.add(
            TextMessage.createForRemoteUser(
                message,
                System.currentTimeMillis(),
                UUID.randomUUID().toString()
            )
        )
        var reply: String? = null
        smartReplyGenerator.suggestReplies(chatHistory).continueWith { task ->
            when (task.result.status) {
                SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE -> {
                    Log.d("SmartReplyManager", "Not supported language")
                    reply = null
                }
                SmartReplySuggestionResult.STATUS_NO_REPLY -> {
                    Log.d("SmartReplyManager", "No reply")
                    reply = null
                }

                SmartReplySuggestionResult.STATUS_SUCCESS -> {
                    Log.d("SmartReplyManager", "Success ${task.result.suggestions.first().text}")
                    reply = task.result.suggestions.first().text
                }
            }
        }.addOnFailureListener {
            Log.d("SmartReplyManager", "Error ${it.message}")
            reply = null
        }
        return reply
    }

    override fun generateReply(
        trigger: String,
        isGroupMessage:Boolean,
        rule: AutoReplyEntity
    ): String? {
        return generateSmartReply(trigger)
    }
}