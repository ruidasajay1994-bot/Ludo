package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // "DEPOSIT", "WITHDRAWAL", "CONTEST_ENTRY", "CONTEST_WIN", "DAILY_SPIN", "REFERRAL_BONUS"
    val title: String,
    val amount: Double,
    val isCredit: Boolean,
    val status: String = "SUCCESS", // "SUCCESS", "PENDING"
    val timestamp: Long = System.currentTimeMillis(),
    val referenceId: String = ""
)
