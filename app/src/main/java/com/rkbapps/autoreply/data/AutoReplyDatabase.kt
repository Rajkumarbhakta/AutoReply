package com.rkbapps.autoreply.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AutoReplyEntity::class], version = 1, exportSchema = false)
abstract class AutoReplyDatabase: RoomDatabase() {
    abstract val dao: AutoReplyDao
}