package com.rkbapps.autoreply.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rkbapps.autoreply.models.Contact

class Converters {
    val gson = Gson()

    @TypeConverter
    fun fromReplySchedule(value: ReplySchedule?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toReplySchedule(value: String?): ReplySchedule? {
        return value?.let { gson.fromJson(it, ReplySchedule::class.java) }
    }

    @TypeConverter
    fun fromContactList(value: List<Contact>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toContactList(value: String): List<Contact> {
        val listType = object : TypeToken<List<Contact>>() {}.type
        return gson.fromJson(value, listType)
    }

}