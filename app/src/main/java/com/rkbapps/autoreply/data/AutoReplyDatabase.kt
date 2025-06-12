package com.rkbapps.autoreply.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [AutoReplyEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AutoReplyDatabase : RoomDatabase() {
    abstract val dao: AutoReplyDao
}