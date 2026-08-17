package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.data.local.entities.MatchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchHistoryDao {
    @Query("SELECT * FROM match_history ORDER BY timestamp DESC")
    fun getAllMatches(): Flow<List<MatchHistoryEntity>>

    @Insert
    suspend fun insertMatch(match: MatchHistoryEntity): Long
}
