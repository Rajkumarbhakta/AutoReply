package com.rkbapps.autoreply.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    val gson = Gson()
    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return gson.toJson(list)
    }
    @TypeConverter
    fun toStringList(json: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun fromReplyScheduleList(value: List<ReplySchedule>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toReplyScheduleList(value: String): List<ReplySchedule> {
        val listType = object : TypeToken<List<ReplySchedule>>() {}.type
        return gson.fromJson(value, listType)
    }

}