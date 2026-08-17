package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "match_history")
data class MatchHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mode: String, // "QUICK_1V1", "CLASSIC_4P", "SPEED_RUSH", "TOURNAMENT"
    val entryFee: Double,
    val prizeWon: Double,
    val rank: Int, // 1, 2, 3, 4
    val playerScore: Int,
    val opponentName: String,
    val result: String, // "WON", "LOST"
    val timestamp: Long = System.currentTimeMillis()
)
