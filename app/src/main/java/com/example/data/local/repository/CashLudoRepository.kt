package com.example.data.local.repository

import com.example.data.local.dao.MatchHistoryDao
import com.example.data.local.dao.TransactionDao
import com.example.data.local.dao.WalletDao
import com.example.data.local.entities.MatchHistoryEntity
import com.example.data.local.entities.TransactionEntity
import com.example.data.local.entities.UserWalletEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class CashLudoRepository(
    private val walletDao: WalletDao,
    private val transactionDao: TransactionDao,
    private val matchHistoryDao: MatchHistoryDao
) {
    val walletFlow: Flow<UserWalletEntity?> = walletDao.getWallet()
    val transactionsFlow: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val matchesFlow: Flow<List<MatchHistoryEntity>> = matchHistoryDao.getAllMatches()

    suspend fun getWallet(): UserWalletEntity {
        return walletDao.getWalletDirect() ?: UserWalletEntity().also {
            walletDao.insertOrUpdate(it)
        }
    }

    suspend fun addCash(amount: Double, paymentMethod: String): Boolean {
        val current = getWallet()
        val updated = current.copy(depositBalance = current.depositBalance + amount)
        walletDao.updateWallet(updated)
        transactionDao.insertTransaction(
            TransactionEntity(
                type = "DEPOSIT",
                title = "Added Cash via $paymentMethod",
                amount = amount,
                isCredit = true,
                referenceId = "DEP_" + UUID.randomUUID().toString().take(8).uppercase()
            )
        )
        return true
    }

    suspend fun withdrawCash(amount: Double, accountDetails: String): Boolean {
        val current = getWallet()
        if (current.winningsBalance < amount) return false
        val updated = current.copy(winningsBalance = current.winningsBalance - amount)
        walletDao.updateWallet(updated)
        transactionDao.insertTransaction(
            TransactionEntity(
                type = "WITHDRAWAL",
                title = "Withdrawal to $accountDetails",
                amount = amount,
                isCredit = false,
                referenceId = "WDR_" + UUID.randomUUID().toString().take(8).uppercase()
            )
        )
        return true
    }

    suspend fun deductEntryFee(fee: Double): Boolean {
        val current = getWallet()
        if (current.totalBalance < fee) return false

        // Deduct from Bonus first (up to 10%), then Deposit, then Winnings
        var remaining = fee
        var newBonus = current.bonusBalance
        var newDeposit = current.depositBalance
        var newWinnings = current.winningsBalance

        // 10% bonus usage discount if available
        val maxBonusToUse = minOf(fee * 0.20, newBonus)
        newBonus -= maxBonusToUse
        remaining -= maxBonusToUse

        if (newDeposit >= remaining) {
            newDeposit -= remaining
            remaining = 0.0
        } else {
            remaining -= newDeposit
            newDeposit = 0.0
            newWinnings = maxOf(0.0, newWinnings - remaining)
        }

        walletDao.updateWallet(
            current.copy(
                depositBalance = newDeposit,
                winningsBalance = newWinnings,
                bonusBalance = newBonus,
                matchesPlayed = current.matchesPlayed + 1
            )
        )

        transactionDao.insertTransaction(
            TransactionEntity(
                type = "CONTEST_ENTRY",
                title = "Contest Entry Fee",
                amount = fee,
                isCredit = false,
                referenceId = "FEE_" + UUID.randomUUID().toString().take(8).uppercase()
            )
        )
        return true
    }

    suspend fun creditWinnings(
        prize: Double,
        mode: String,
        isWin: Boolean,
        rank: Int,
        playerScore: Int,
        opponentName: String,
        entryFee: Double
    ) {
        val current = getWallet()
        val newWinnings = if (isWin) current.winningsBalance + prize else current.winningsBalance
        val newTotalWon = if (isWin) current.totalWon + prize else current.totalWon
        val newMatchesWon = if (isWin) current.matchesWon + 1 else current.matchesWon
        val newStreak = if (isWin) current.currentStreak + 1 else 0
        val newHighestStreak = maxOf(current.highestStreak, newStreak)

        walletDao.updateWallet(
            current.copy(
                winningsBalance = newWinnings,
                totalWon = newTotalWon,
                matchesWon = newMatchesWon,
                currentStreak = newStreak,
                highestStreak = newHighestStreak
            )
        )

        if (isWin && prize > 0) {
            transactionDao.insertTransaction(
                TransactionEntity(
                    type = "CONTEST_WIN",
                    title = "$mode Cash Prize Winnings",
                    amount = prize,
                    isCredit = true,
                    referenceId = "WIN_" + UUID.randomUUID().toString().take(8).uppercase()
                )
            )
        }

        matchHistoryDao.insertMatch(
            MatchHistoryEntity(
                mode = mode,
                entryFee = entryFee,
                prizeWon = prize,
                rank = rank,
                playerScore = playerScore,
                opponentName = opponentName,
                result = if (isWin) "WON" else "LOST"
            )
        )
    }

    suspend fun claimSpinReward(bonusAmount: Double) {
        val current = getWallet()
        val updated = current.copy(
            bonusBalance = current.bonusBalance + bonusAmount,
            lastSpinTimestamp = System.currentTimeMillis()
        )
        walletDao.updateWallet(updated)
        transactionDao.insertTransaction(
            TransactionEntity(
                type = "DAILY_SPIN",
                title = "Daily Lucky Spin Bonus",
                amount = bonusAmount,
                isCredit = true,
                referenceId = "SPN_" + UUID.randomUUID().toString().take(8).uppercase()
            )
        )
    }

    suspend fun updateProfile(name: String, avatarId: Int, diceSkin: String) {
        val current = getWallet()
        walletDao.updateWallet(
            current.copy(
                playerName = name,
                playerAvatarId = avatarId,
                selectedDiceSkin = diceSkin
            )
        )
    }
}
