package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.MatchHistoryDao
import com.example.data.local.dao.TransactionDao
import com.example.data.local.dao.WalletDao
import com.example.data.local.entities.MatchHistoryEntity
import com.example.data.local.entities.TransactionEntity
import com.example.data.local.entities.UserWalletEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserWalletEntity::class,
        TransactionEntity::class,
        MatchHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun walletDao(): WalletDao
    abstract fun transactionDao(): TransactionDao
    abstract fun matchHistoryDao(): MatchHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cash_ludo_database.db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getDatabase(context)
                            // Populate default user wallet & sample transactions
                            database.walletDao().insertOrUpdate(
                                UserWalletEntity(
                                    id = 1,
                                    depositBalance = 250.0,
                                    winningsBalance = 120.0,
                                    bonusBalance = 50.0,
                                    totalWon = 680.0,
                                    matchesPlayed = 24,
                                    matchesWon = 18,
                                    highestStreak = 6,
                                    currentStreak = 3,
                                    selectedDiceSkin = "ROYAL_GOLD",
                                    playerName = "CashMaster_07",
                                    playerAvatarId = 0
                                )
                            )
                            database.transactionDao().insertTransaction(
                                TransactionEntity(
                                    type = "DEPOSIT",
                                    title = "Instant UPI Add Cash",
                                    amount = 100.0,
                                    isCredit = true,
                                    referenceId = "TXN_UPI_98231",
                                    timestamp = System.currentTimeMillis() - 86400000L
                                )
                            )
                            database.transactionDao().insertTransaction(
                                TransactionEntity(
                                    type = "CONTEST_WIN",
                                    title = "1v1 Quick Cash Duel Win",
                                    amount = 90.0,
                                    isCredit = true,
                                    referenceId = "WIN_LUDO_44129",
                                    timestamp = System.currentTimeMillis() - 43200000L
                                )
                            )
                            database.matchHistoryDao().insertMatch(
                                MatchHistoryEntity(
                                    mode = "QUICK_1V1",
                                    entryFee = 50.0,
                                    prizeWon = 90.0,
                                    rank = 1,
                                    playerScore = 184,
                                    opponentName = "SpeedDemon",
                                    result = "WON",
                                    timestamp = System.currentTimeMillis() - 43200000L
                                )
                            )
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
