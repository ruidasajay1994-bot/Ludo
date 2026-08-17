package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.UserWalletEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {
    @Query("SELECT * FROM user_wallet WHERE id = 1")
    fun getWallet(): Flow<UserWalletEntity?>

    @Query("SELECT * FROM user_wallet WHERE id = 1")
    suspend fun getWalletDirect(): UserWalletEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(wallet: UserWalletEntity)

    @Update
    suspend fun updateWallet(wallet: UserWalletEntity)
}
