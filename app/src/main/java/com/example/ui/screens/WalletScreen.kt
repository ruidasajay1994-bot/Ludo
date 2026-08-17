package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.TransactionEntity
import com.example.data.local.entities.UserWalletEntity
import com.example.ui.components.AddCashDialog
import com.example.ui.components.WalletComponent
import com.example.ui.components.WithdrawCashDialog
import com.example.ui.theme.CashGreen
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.TextMuted

@Composable
fun WalletScreen(
    wallet: UserWalletEntity?,
    transactions: List<TransactionEntity>,
    selectedFilter: String,
    onFilterSelect: (String) -> Unit,
    showAddCashDialog: Boolean,
    showWithdrawDialog: Boolean,
    onShowAddCash: (Boolean) -> Unit,
    onShowWithdraw: (Boolean) -> Unit,
    onAddCashSubmit: (Double, String) -> Unit,
    onWithdrawSubmit: (Double, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .testTag("wallet_screen")
    ) {
        // App Bar Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = DarkNavySurface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CASH WALLET",
                    color = GoldPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = CashGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "SECURE",
                        color = CashGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // Main Vibrant Wallet Component
        WalletComponent(
            wallet = wallet,
            transactions = transactions,
            selectedFilter = selectedFilter,
            onFilterSelect = onFilterSelect,
            onDepositClick = { onShowAddCash(true) },
            onWithdrawClick = { onShowWithdraw(true) },
            onQuickDepositPreset = { amount ->
                onAddCashSubmit(amount, "Instant UPI")
            },
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp)
        )
    }

    // Add Cash / Deposit Dialog
    if (showAddCashDialog) {
        AddCashDialog(
            onDismiss = { onShowAddCash(false) },
            onAddCash = onAddCashSubmit
        )
    }

    // Withdraw Dialog
    if (showWithdrawDialog) {
        WithdrawCashDialog(
            winningsBalance = wallet?.winningsBalance ?: 0.0,
            onDismiss = { onShowWithdraw(false) },
            onWithdraw = onWithdrawSubmit
        )
    }
}

