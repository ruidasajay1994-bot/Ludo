package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_wallet")
data class UserWalletEntity(
    @PrimaryKey val id: Int = 1,
    val depositBalance: Double = 150.0,
    val winningsBalance: Double = 85.0,
    val bonusBalance: Double = 35.0,
    val totalWon: Double = 420.0,
    val matchesPlayed: Int = 18,
    val matchesWon: Int = 14,
    val highestStreak: Int = 5,
    val currentStreak: Int = 2,
    val lastSpinTimestamp: Long = 0L,
    val selectedDiceSkin: String = "ROYAL_GOLD",
    val playerName: String = "CashKing_99",
    val playerAvatarId: Int = 0
) {
    val totalBalance: Double
        get() = depositBalance + winningsBalance + bonusBalance
}
