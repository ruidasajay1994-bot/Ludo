package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entities.MatchHistoryEntity
import com.example.data.local.entities.TransactionEntity
import com.example.data.local.entities.UserWalletEntity
import com.example.data.local.repository.CashLudoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WalletUiState(
    val isAddCashDialogVisible: Boolean = false,
    val isWithdrawDialogVisible: Boolean = false,
    val isSpinWheelDialogVisible: Boolean = false,
    val isScratchCardVisible: Boolean = false,
    val isEditProfileDialogVisible: Boolean = false,
    val selectedTxnFilter: String = "ALL", // "ALL", "DEPOSIT", "WINNING", "WITHDRAWAL"
    val lastRewardClaimed: Double? = null,
    val statusMessage: String? = null
)

class WalletViewModel(
    private val repository: CashLudoRepository
) : ViewModel() {

    val wallet: StateFlow<UserWalletEntity?> = repository.walletFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val transactions: StateFlow<List<TransactionEntity>> = repository.transactionsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val matchHistory: StateFlow<List<MatchHistoryEntity>> = repository.matchesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(WalletUiState())
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

    fun showAddCash(show: Boolean) {
        _uiState.update { it.copy(isAddCashDialogVisible = show) }
    }

    fun showWithdraw(show: Boolean) {
        _uiState.update { it.copy(isWithdrawDialogVisible = show) }
    }

    fun showSpinWheel(show: Boolean) {
        _uiState.update { it.copy(isSpinWheelDialogVisible = show) }
    }

    fun showScratchCard(show: Boolean) {
        _uiState.update { it.copy(isScratchCardVisible = show) }
    }

    fun showEditProfile(show: Boolean) {
        _uiState.update { it.copy(isEditProfileDialogVisible = show) }
    }

    fun setTxnFilter(filter: String) {
        _uiState.update { it.copy(selectedTxnFilter = filter) }
    }

    fun addCash(amount: Double, method: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.addCash(amount, method)
            _uiState.update {
                it.copy(
                    isAddCashDialogVisible = false,
                    statusMessage = "Successfully added ₹${"%.2f".format(amount)} to Cash Wallet!"
                )
            }
            onComplete()
        }
    }

    fun withdraw(amount: Double, account: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val current = repository.getWallet()
            if (amount < 20.0) {
                onResult(false, "Minimum withdrawal amount is ₹20.00")
                return@launch
            }
            if (current.winningsBalance < amount) {
                onResult(false, "Insufficient winnings balance. Available: ₹${"%.2f".format(current.winningsBalance)}")
                return@launch
            }
            val success = repository.withdrawCash(amount, account)
            if (success) {
                _uiState.update {
                    it.copy(
                        isWithdrawDialogVisible = false,
                        statusMessage = "Withdrawal request of ₹${"%.2f".format(amount)} processed successfully!"
                    )
                }
                onResult(true, "Withdrawal successful! Sent to $account")
            } else {
                onResult(false, "Withdrawal failed. Please try again.")
            }
        }
    }

    fun claimSpinBonus(amount: Double) {
        viewModelScope.launch {
            repository.claimSpinReward(amount)
            _uiState.update {
                it.copy(
                    lastRewardClaimed = amount,
                    statusMessage = "You won ₹${"%.2f".format(amount)} Bonus Cash!"
                )
            }
        }
    }

    fun updateProfile(name: String, avatarId: Int, diceSkin: String) {
        viewModelScope.launch {
            repository.updateProfile(name, avatarId, diceSkin)
            _uiState.update {
                it.copy(
                    isEditProfileDialogVisible = false,
                    statusMessage = "Profile updated successfully!"
                )
            }
        }
    }

    fun clearStatusMessage() {
        _uiState.update { it.copy(statusMessage = null, lastRewardClaimed = null) }
    }
}
