package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.TransactionEntity
import com.example.data.local.entities.UserWalletEntity
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentMagenta
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.CashGreen
import com.example.ui.theme.CashGreenDark
import com.example.ui.theme.CashGreenLight
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.DarkNavyBorder
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavyElevated
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.LudoBlue
import com.example.ui.theme.LudoGreen
import com.example.ui.theme.LudoRed
import com.example.ui.theme.LudoYellow
import com.example.ui.theme.TextGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TextWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Reusable, rich Wallet Component adhering to the Vibrant Theme.
 * Displays:
 * 1. Current Balance (Total with 3-tier sub-wallet breakdown: Deposit, Winnings, Bonus).
 * 2. Prominent Vibrant 'Deposit' button & quick deposit action triggers.
 * 3. Filterable Transaction History with status, type icons, timestamps, and amounts.
 */
@Composable
fun WalletComponent(
    wallet: UserWalletEntity?,
    transactions: List<TransactionEntity>,
    selectedFilter: String = "ALL",
    onFilterSelect: (String) -> Unit = {},
    onDepositClick: () -> Unit,
    onWithdrawClick: (() -> Unit)? = null,
    onQuickDepositPreset: ((Double) -> Unit)? = null,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp)
) {
    val filteredTxns = when (selectedFilter) {
        "DEPOSITS" -> transactions.filter { it.type == "DEPOSIT" }
        "WINNINGS" -> transactions.filter { it.type == "CONTEST_WIN" }
        "WITHDRAWALS" -> transactions.filter { it.type == "WITHDRAWAL" }
        "BONUS" -> transactions.filter { it.type == "BONUS" || it.title.contains("Bonus", ignoreCase = true) }
        else -> transactions
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .testTag("wallet_component"),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Current Balance Vibrant Card
        item {
            WalletCurrentBalanceCard(
                wallet = wallet,
                onDepositClick = onDepositClick,
                onWithdrawClick = onWithdrawClick,
                onQuickDepositPreset = onQuickDepositPreset
            )
        }

        // 2. Sub-Balance Breakdown Section (Deposit vs Winnings vs Bonus)
        item {
            WalletBreakdownSection(wallet = wallet)
        }

        // 3. Transaction History Header & Filter Chips
        item {
            TransactionHistoryHeader(
                selectedFilter = selectedFilter,
                onFilterSelect = onFilterSelect,
                totalCount = filteredTxns.size
            )
        }

        // 4. Transaction Items List
        if (filteredTxns.isEmpty()) {
            item {
                EmptyTransactionsState(selectedFilter = selectedFilter)
            }
        } else {
            items(filteredTxns, key = { it.id }) { txn ->
                VibrantTransactionItem(txn = txn)
            }
        }

        item {
            // Security badge at bottom
            SecurityGuaranteedBadge()
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Main Current Balance Card featuring vibrant gold/emerald gradient glow,
 * prominent 'Deposit' button, and quick-add preset chips.
 */
@Composable
fun WalletCurrentBalanceCard(
    wallet: UserWalletEntity?,
    onDepositClick: () -> Unit,
    onWithdrawClick: (() -> Unit)?,
    onQuickDepositPreset: ((Double) -> Unit)?,
    modifier: Modifier = Modifier
) {
    val totalBalance = wallet?.totalBalance ?: 0.0
    val winningsBalance = wallet?.winningsBalance ?: 0.0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(24.dp), ambientColor = GoldPrimary, spotColor = AccentPurple)
            .testTag("wallet_balance_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkNavyCard)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            DarkNavyElevated,
                            DarkNavyCard,
                            DarkNavySurface
                        )
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(GoldPrimary, AccentMagenta, AccentPurple)
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header badge row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = "Wallet",
                                tint = GoldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "TOTAL CASH BALANCE",
                            color = TextGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp
                        )
                    }

                    // Verified Badge
                    Surface(
                        color = CashGreen.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CashGreen.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = CashGreen,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ACTIVE",
                                color = CashGreenLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Big Balance Display
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "₹",
                        color = GoldSecondary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "%.2f".format(totalBalance),
                        color = GoldPrimary,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp,
                        modifier = Modifier.testTag("current_balance_text")
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    // Withdrawable note
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Withdrawable",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "₹${"%.2f".format(winningsBalance)}",
                            color = CashGreenLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Primary 'Deposit' and 'Withdraw' Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // DEPOSIT BUTTON (Adhering to Vibrant Theme)
                    Button(
                        onClick = onDepositClick,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CashGreen,
                            contentColor = Color(0xFF0F172A)
                        ),
                        modifier = Modifier
                            .weight(1.2f)
                            .height(48.dp)
                            .shadow(8.dp, RoundedCornerShape(14.dp), spotColor = CashGreen)
                            .testTag("deposit_button")
                            .testTag("wallet_add_cash_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0F172A).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color(0xFF0F172A)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "DEPOSIT CASH",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    // WITHDRAW BUTTON
                    if (onWithdrawClick != null) {
                        OutlinedButton(
                            onClick = onWithdrawClick,
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, DarkNavyBorder),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = DarkNavyElevated,
                                contentColor = TextWhite
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("wallet_withdraw_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = GoldPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "WITHDRAW",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = TextWhite
                            )
                        }
                    }
                }

                // Quick Deposit Chips
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quick:",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    listOf(50.0, 100.0, 250.0, 500.0).forEach { preset ->
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, DarkNavyBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    if (onQuickDepositPreset != null) {
                                        onQuickDepositPreset(preset)
                                    } else {
                                        onDepositClick()
                                    }
                                }
                                .testTag("quick_deposit_${preset.toInt()}"),
                            color = DarkNavySurface
                        ) {
                            Text(
                                text = "+₹${preset.toInt()}",
                                color = GoldPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 3-Tier Wallet Breakdown (Deposit Cash, Winnings Cash, Bonus Cash)
 */
@Composable
fun WalletBreakdownSection(
    wallet: UserWalletEntity?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "WALLET BREAKDOWN",
                color = TextWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Text(
                text = "3 Accounts",
                color = TextMuted,
                fontSize = 11.sp
            )
        }

        // 1. Deposit Wallet Row
        VibrantWalletDetailCard(
            title = "Deposit Cash",
            subtitle = "Used directly to enter cash battles & contests",
            amount = wallet?.depositBalance ?: 0.0,
            icon = Icons.Default.Payment,
            accentColor = GoldPrimary,
            tag = "deposit_subwallet"
        )

        // 2. Winnings Wallet Row
        VibrantWalletDetailCard(
            title = "Winnings Cash",
            subtitle = "Directly withdrawable instantly to Bank or UPI",
            amount = wallet?.winningsBalance ?: 0.0,
            icon = Icons.Default.EmojiEvents,
            accentColor = CashGreen,
            tag = "winnings_subwallet"
        )

        // 3. Bonus Wallet Row
        VibrantWalletDetailCard(
            title = "Bonus Cash",
            subtitle = "Applied automatically as entry fee discounts",
            amount = wallet?.bonusBalance ?: 0.0,
            icon = Icons.Default.CardGiftcard,
            accentColor = AccentPurple,
            tag = "bonus_subwallet"
        )
    }
}

@Composable
fun VibrantWalletDetailCard(
    title: String,
    subtitle: String,
    amount: Double,
    icon: ImageVector,
    accentColor: Color,
    tag: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, DarkNavyBorder.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .testTag(tag),
        color = DarkNavyCard
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.15f))
                        .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = subtitle,
                        color = TextMuted,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "₹${"%.2f".format(amount)}",
                color = accentColor,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp
            )
        }
    }
}

/**
 * Transaction History Header with vibrant filter chips
 */
@Composable
fun TransactionHistoryHeader(
    selectedFilter: String,
    onFilterSelect: (String) -> Unit,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = null,
                    tint = AccentCyan,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "TRANSACTION HISTORY",
                    color = TextWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp
                )
            }

            Text(
                text = "$totalCount entries",
                color = TextMuted,
                fontSize = 11.sp
            )
        }

        // Filter Chips Row
        val filterOptions = listOf(
            Pair("ALL", "All"),
            Pair("DEPOSITS", "Deposits"),
            Pair("WINNINGS", "Winnings"),
            Pair("WITHDRAWALS", "Withdrawals"),
            Pair("BONUS", "Bonus")
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filterOptions) { (key, label) ->
                val isSelected = selectedFilter == key
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .border(
                            width = 1.dp,
                            color = if (isSelected) GoldPrimary else DarkNavyBorder,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { onFilterSelect(key) }
                        .testTag("filter_txn_$key"),
                    color = if (isSelected) GoldPrimary else DarkNavyCard
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color(0xFF0F172A) else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
            }
        }
    }
}

/**
 * Individual Vibrant Transaction Item Card with Type Icon, Timestamp, Status, and Colored Amount
 */
@Composable
fun VibrantTransactionItem(
    txn: TransactionEntity,
    modifier: Modifier = Modifier
) {
    val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(txn.timestamp))
    val isCredit = txn.isCredit
    val isWin = txn.type == "CONTEST_WIN"
    val isDeposit = txn.type == "DEPOSIT"
    val isWithdrawal = txn.type == "WITHDRAWAL"

    val iconColor = when {
        isWin -> GoldPrimary
        isDeposit -> CashGreen
        isWithdrawal -> AccentOrange
        else -> AccentPurple
    }

    val iconVector = when {
        isWin -> Icons.Default.EmojiEvents
        isDeposit -> Icons.Default.ArrowDownward
        isWithdrawal -> Icons.Default.ArrowUpward
        else -> Icons.Default.CardGiftcard
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, DarkNavyBorder.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .testTag("txn_item_${txn.id}"),
        color = DarkNavyCard
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon & Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.15f))
                        .border(1.dp, iconColor.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = txn.title,
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$dateStr • ${txn.referenceId}",
                        color = TextMuted,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Amount and status
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isCredit) "+" else "-"}₹${"%.2f".format(txn.amount)}",
                    color = if (isCredit) CashGreenLight else LudoRed,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Surface(
                    color = if (txn.status == "SUCCESS" || txn.status == "COMPLETED") CashGreen.copy(alpha = 0.15f) else DarkNavyElevated,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = txn.status,
                        color = if (txn.status == "SUCCESS" || txn.status == "COMPLETED") CashGreenLight else TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

/**
 * Empty State when no transactions match filter
 */
@Composable
fun EmptyTransactionsState(
    selectedFilter: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkNavyCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(DarkNavyElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No $selectedFilter Transactions",
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Play contests, deposit cash, or win matches to view activity here.",
                color = TextMuted,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * 100% Safe & Secure Encryption Badge
 */
@Composable
fun SecurityGuaranteedBadge(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, DarkNavyBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
        color = DarkNavyCard.copy(alpha = 0.7f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = CashGreen,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "256-Bit SSL Encrypted • Instant Real-Time Deposits",
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
