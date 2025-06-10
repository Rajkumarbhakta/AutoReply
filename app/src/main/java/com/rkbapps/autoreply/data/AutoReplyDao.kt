package com.rkbapps.autoreply.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AutoReplyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAutoReply(autoReply: AutoReplyEntity)

    @Delete
    suspend fun deleteAutoReply(autoReply: AutoReplyEntity)

    @Update
    suspend fun updateAutoReply(autoReply: AutoReplyEntity)

    @Query("SELECT * FROM auto_reply WHERE id = :id")
    suspend fun getAutoReplyById(id: Int): AutoReplyEntity?

    @Query("SELECT * FROM auto_reply")
    fun getAllAutoReplies(): Flow<List<AutoReplyEntity>>

    @Query("DELETE FROM auto_reply WHERE id = :id")
    suspend fun deleteAutoReplyById(id: Int)

    @Query("UPDATE auto_reply SET isActive = :isActive WHERE id = :id")
    suspend fun updateAutoReplyActiveStatus(id: Int, isActive: Boolean)

    @Query("SELECT * FROM auto_reply WHERE isActive = 1")
    fun getActiveAutoReplies(): Flow<List<AutoReplyEntity>>

    @Query("SELECT * FROM auto_reply WHERE isActive = 1")
    suspend fun getAllActiveAutoRepliesOnce(): List<AutoReplyEntity>

    @Query("SELECT * FROM auto_reply WHERE isActive = 0")
    fun getInactiveAutoReplies(): Flow<List<AutoReplyEntity>>


}