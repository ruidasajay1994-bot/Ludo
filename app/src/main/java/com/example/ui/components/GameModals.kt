package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.game.model.ContestTier
import com.example.game.model.Player
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.CashGreen
import com.example.ui.theme.CashGreenDark
import com.example.ui.theme.DarkNavyBg
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
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun VictoryCashoutDialog(
    winner: Player?,
    isHumanWinner: Boolean,
    contest: ContestTier?,
    onPlayAgain: () -> Unit,
    onBackToLobby: () -> Unit
) {
    val prize = if (isHumanWinner && contest != null) contest.firstPrize else 0.0
    val infiniteTransition = rememberInfiniteTransition(label = "trophyBounce")
    val trophyScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "trophyScale"
    )

    Dialog(onDismissRequest = onBackToLobby) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(24.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, Brush.linearGradient(listOf(GoldPrimary, GoldDark, DarkNavyElevated)), RoundedCornerShape(24.dp))
                .testTag("victory_dialog"),
            color = DarkNavySurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Trophy Icon
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .scale(trophyScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                if (isHumanWinner) listOf(GoldPrimary, GoldDark, Color(0xFF0F172A))
                                else listOf(DarkNavyElevated, DarkNavyCard)
                            )
                        )
                        .border(2.dp, if (isHumanWinner) GoldPrimary else TextMuted, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isHumanWinner) "🏆" else "🎮",
                        fontSize = 44.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isHumanWinner) "WINNER WINNER!" else "MATCH FINISHED",
                    color = if (isHumanWinner) GoldPrimary else TextWhite,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                Text(
                    text = if (isHumanWinner) "Congratulations! You won the cash prize!" else "Winner: ${winner?.name ?: "Opponent"}",
                    color = TextMuted,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Cash Prize Box
                if (isHumanWinner && prize > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(listOf(Color(0xFF052E16), Color(0xFF065F46)))
                            )
                            .border(1.5.dp, CashGreen, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "CASH PRIZE CREDITED",
                                color = CashGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "+₹${"%.2f".format(prize)}",
                                color = TextWhite,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Added directly to your Winnings Wallet",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Button(
                    onClick = onPlayAgain,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("play_again_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color(0xFF0F172A))
                ) {
                    Text(
                        text = "PLAY AGAIN 🎲",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onBackToLobby,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("back_to_lobby_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkNavyElevated, contentColor = TextWhite)
                ) {
                    Text(
                        text = "LOBBY & WALLET",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickChatDialog(
    onDismiss: () -> Unit,
    onSendMessage: (String, Boolean) -> Unit
) {
    val quickMessages = listOf(
        "Roll a 6 please! 🎲",
        "Good luck! 👍",
        "Well played! 👏",
        "Nice move! 🔥",
        "Oops! 🙈",
        "Hurry up! ⏳",
        "GG! 🏆",
        "Rematch? 💥"
    )

    val emojis = listOf("🎲", "🔥", "😂", "😎", "😱", "👍", "👑", "💰", "💥", "🥳")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, DarkNavyElevated, RoundedCornerShape(20.dp))
                .testTag("quick_chat_dialog"),
            color = DarkNavySurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "QUICK CHAT & TAUNTS",
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 1.sp
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Emojis Row
                Text(text = "Express with Emojis", color = TextMuted, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    emojis.forEach { emoji ->
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(DarkNavyElevated)
                                .clickable {
                                    onSendMessage(emoji, true)
                                    onDismiss()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 20.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Messages
                Text(text = "Quick Messages", color = TextMuted, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    quickMessages.forEach { msg ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    onSendMessage(msg, false)
                                    onDismiss()
                                },
                            color = DarkNavyElevated
                        ) {
                            Text(
                                text = msg,
                                color = TextWhite,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SpinWheelDialog(
    onDismiss: () -> Unit,
    onClaimReward: (Double) -> Unit
) {
    val prizes = listOf(
        Pair("₹10 Bonus", 10.0),
        Pair("₹25 Cash", 25.0),
        Pair("₹50 Bonus", 50.0),
        Pair("₹100 Cash", 100.0),
        Pair("₹15 Bonus", 15.0),
        Pair("₹250 Jackpot", 250.0),
        Pair("₹20 Bonus", 20.0),
        Pair("₹500 Mega", 500.0)
    )

    val wheelColors = listOf(
        LudoRed, LudoBlue, LudoGreen, LudoYellow,
        AccentPurple, AccentOrange, CashGreen, GoldDark
    )

    val rotation = remember { Animatable(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    var wonPrize by remember { mutableStateOf<Pair<String, Double>?>(null) }
    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = { if (!isSpinning) onDismiss() }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, GoldPrimary, RoundedCornerShape(24.dp))
                .testTag("spin_wheel_dialog"),
            color = DarkNavySurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎡 DAILY LUCKY CASH SPIN",
                        color = GoldPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                    if (!isSpinning) {
                        IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Wheel Visual with Needle
                Box(
                    modifier = Modifier
                        .size(230.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Wheel Canvas
                    Canvas(
                        modifier = Modifier
                            .size(220.dp)
                            .rotate(rotation.value)
                    ) {
                        val radius = size.minDimension / 2
                        val center = Offset(size.width / 2, size.height / 2)
                        val anglePerSegment = 360f / prizes.size

                        for (i in prizes.indices) {
                            val startAngle = i * anglePerSegment
                            drawArc(
                                color = wheelColors[i % wheelColors.size],
                                startAngle = startAngle,
                                sweepAngle = anglePerSegment,
                                useCenter = true,
                                size = size
                            )
                        }

                        // Outer gold border
                        drawCircle(
                            color = GoldPrimary,
                            radius = radius,
                            center = center,
                            style = Stroke(width = 6.dp.toPx())
                        )
                        // Inner center knob
                        drawCircle(
                            color = Color(0xFF0F172A),
                            radius = 24.dp.toPx(),
                            center = center
                        )
                        drawCircle(
                            color = GoldPrimary,
                            radius = 16.dp.toPx(),
                            center = center
                        )
                    }

                    // Top Pointer Needle
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .size(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🔻", fontSize = 24.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (wonPrize != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CashGreen.copy(alpha = 0.2f))
                            .border(1.dp, CashGreen, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🎉 YOU WON ${wonPrize!!.first}!",
                            color = CashGreen,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Spin Action Button
                Button(
                    onClick = {
                        if (!isSpinning && wonPrize == null) {
                            isSpinning = true
                            val targetIndex = Random.nextInt(prizes.size)
                            val selected = prizes[targetIndex]
                            val degreesPerItem = 360f / prizes.size
                            val targetDegrees = (360f * 5) + (360f - (targetIndex * degreesPerItem + degreesPerItem / 2))
                            scope.launch {
                                rotation.animateTo(
                                    targetValue = targetDegrees,
                                    animationSpec = tween(3500, easing = FastOutSlowInEasing)
                                )
                                isSpinning = false
                                wonPrize = selected
                                onClaimReward(selected.second)
                            }
                        } else if (wonPrize != null) {
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("spin_wheel_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (wonPrize != null) CashGreen else GoldPrimary,
                        contentColor = Color(0xFF0F172A)
                    )
                ) {
                    Text(
                        text = when {
                            isSpinning -> "SPINNING..."
                            wonPrize != null -> "CLAIM BONUS CASH"
                            else -> "SPIN FREE NOW 🎯"
                        },
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AddCashDialog(
    onDismiss: () -> Unit,
    onAddCash: (Double, String) -> Unit
) {
    var amountText by remember { mutableStateOf("100") }
    var selectedMethod by remember { mutableStateOf("UPI (GPay / PhonePe / Paytm)") }
    val presets = listOf(50.0, 100.0, 250.0, 500.0)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, Brush.linearGradient(listOf(GoldPrimary, DarkNavyElevated)), RoundedCornerShape(24.dp))
                .testTag("add_cash_dialog"),
            color = DarkNavySurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "₹",
                            color = GoldPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ADD CASH (INDIAN RUPEES)",
                            color = GoldPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Amount Presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presets.forEach { preset ->
                        val isSelected = amountText == preset.toInt().toString()
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    1.dp,
                                    if (isSelected) GoldPrimary else DarkNavyElevated,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { amountText = preset.toInt().toString() }
                                .testTag("preset_amount_${preset.toInt()}"),
                            color = if (isSelected) GoldPrimary.copy(alpha = 0.2f) else DarkNavyElevated
                        ) {
                            Text(
                                text = "+₹${preset.toInt()}",
                                color = if (isSelected) GoldPrimary else TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 10.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Deposit Amount (₹ INR)") },
                    prefix = { Text("₹ ", color = GoldPrimary, fontWeight = FontWeight.Bold) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_cash_amount_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = DarkNavyElevated,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(text = "Instant Indian Payment Gateway", color = TextMuted, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(6.dp))

                val methods = listOf(
                    Pair("UPI (GPay / PhonePe / Paytm)", Icons.Default.QrCode),
                    Pair("NetBanking / Debit Card", Icons.Default.CreditCard)
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    methods.forEach { (methodName, icon) ->
                        val isSel = selectedMethod == methodName
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, if (isSel) CashGreen else DarkNavyElevated, RoundedCornerShape(10.dp))
                                .clickable { selectedMethod = methodName },
                            color = if (isSel) CashGreen.copy(alpha = 0.15f) else DarkNavyElevated
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = icon, contentDescription = null, tint = if (isSel) CashGreen else TextMuted, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = methodName,
                                    color = if (isSel) TextWhite else TextMuted,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Security Note
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = CashGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "100% Safe 256-Bit SSL Encrypted Indian UPI Gateway", color = TextMuted, fontSize = 10.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val amount = amountText.toDoubleOrNull() ?: 0.0
                        if (amount > 0) {
                            onAddCash(amount, selectedMethod)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_add_cash_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CashGreen, contentColor = Color(0xFF0F172A))
                ) {
                    Text(
                        text = "ADD ₹${amountText.ifEmpty { "0" }} SECURELY 💳",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun WithdrawCashDialog(
    winningsBalance: Double,
    onDismiss: () -> Unit,
    onWithdraw: (Double, String) -> Unit
) {
    var amountText by remember { mutableStateOf("50") }
    var accountText by remember { mutableStateOf("player@okhdfcbank") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, Brush.linearGradient(listOf(GoldPrimary, DarkNavyElevated)), RoundedCornerShape(24.dp))
                .testTag("withdraw_dialog"),
            color = DarkNavySurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AccountBalance, contentDescription = null, tint = CashGreen)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "INSTANT UPI / BANK WITHDRAWAL",
                            color = CashGreen,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Balance Available Notice
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp)),
                    color = DarkNavyElevated
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Withdrawable Winnings:", color = TextMuted, fontSize = 12.sp)
                        Text(text = "₹${"%.2f".format(winningsBalance)}", color = CashGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it.filter { ch -> ch.isDigit() }
                        errorMessage = null
                    },
                    label = { Text("Withdraw Amount (₹ INR)") },
                    prefix = { Text("₹ ", color = CashGreen, fontWeight = FontWeight.Bold) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("withdraw_amount_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CashGreen,
                        unfocusedBorderColor = DarkNavyElevated,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = accountText,
                    onValueChange = { accountText = it },
                    label = { Text("UPI ID (e.g. mobile@paytm / name@okaxis)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("withdraw_account_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CashGreen,
                        unfocusedBorderColor = DarkNavyElevated,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = errorMessage!!, color = LudoRed, fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val amt = amountText.toDoubleOrNull() ?: 0.0
                        if (amt < 20.0) {
                            errorMessage = "Min withdrawal is ₹20.00"
                            return@Button
                        }
                        if (amt > winningsBalance) {
                            errorMessage = "Amount exceeds available winnings balance."
                            return@Button
                        }
                        onWithdraw(amt, accountText)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_withdraw_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color(0xFF0F172A))
                ) {
                    Text(
                        text = "WITHDRAW ₹${amountText.ifEmpty { "0" }} INSTANTLY",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
