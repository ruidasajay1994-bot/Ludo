package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.UserWalletEntity
import com.example.ui.theme.CashGreen
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavyElevated
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.LudoBlue
import com.example.ui.theme.LudoRed
import com.example.ui.theme.TextGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

data class DiceSkin(
    val id: String,
    val name: String,
    val description: String,
    val colorPrimary: Color,
    val iconEmoji: String
)

@Composable
fun ProfileScreen(
    wallet: UserWalletEntity?,
    onUpdateProfile: (String, Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditNameDialogVisible by remember { mutableStateOf(false) }
    var soundEnabled by remember { mutableStateOf(true) }
    var hapticsEnabled by remember { mutableStateOf(true) }

    val diceSkins = listOf(
        DiceSkin("ROYAL_GOLD", "Royal Gold", "Pure 24K Gold Pips", GoldPrimary, "✨"),
        DiceSkin("CYBER_NEON", "Cyber Neon", "Electric Glow Dice", CashGreen, "⚡"),
        DiceSkin("CRIMSON_FIRE", "Crimson Flame", "Blazing Ruby Pips", LudoRed, "🔥"),
        DiceSkin("COSMIC_BLUE", "Cosmic Sapphire", "Deep Space Energy", LudoBlue, "🌌")
    )

    val winRate = if (wallet != null && wallet.matchesPlayed > 0) {
        ((wallet.matchesWon.toFloat() / wallet.matchesPlayed.toFloat()) * 100).toInt()
    } else 0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .testTag("profile_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header
        item {
            Surface(modifier = Modifier.fillMaxWidth(), color = DarkNavySurface) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "👤 PLAYER PROFILE",
                        color = GoldPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // 2. Profile Card with Avatar & Name
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .shadow(12.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkNavyCard)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF1E293B), Color(0xFF0F172A), DarkNavyCard)
                            )
                        )
                        .border(1.5.dp, GoldPrimary, RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(listOf(GoldPrimary, GoldDark))
                                    )
                                    .border(2.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "👑", fontSize = 28.sp)
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = wallet?.playerName ?: "CashPlayer",
                                    color = TextWhite,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Tier: Pro Cash Master ⭐",
                                    color = GoldPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        IconButton(
                            onClick = { isEditNameDialogVisible = true },
                            modifier = Modifier.testTag("edit_profile_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = GoldPrimary
                            )
                        }
                    }
                }
            }
        }

        // 3. Stats Grid (4 Stats)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "LIFETIME GAME STATS",
                    color = TextGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatBox(
                        title = "TOTAL WON",
                        value = "₹${"%.2f".format(wallet?.totalWon ?: 0.0)}",
                        color = CashGreen,
                        modifier = Modifier.weight(1f)
                    )
                    StatBox(
                        title = "WIN RATE",
                        value = "$winRate%",
                        color = GoldPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatBox(
                        title = "MATCHES",
                        value = "${wallet?.matchesPlayed ?: 0}",
                        color = TextWhite,
                        modifier = Modifier.weight(1f)
                    )
                    StatBox(
                        title = "WIN STREAK",
                        value = "${wallet?.currentStreak ?: 0} 🔥",
                        color = LudoRed,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 4. Dice Skins Selection
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "🎲 CUSTOM DICE SKINS",
                    color = TextGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                diceSkins.forEach { skin ->
                    val isSelected = (wallet?.selectedDiceSkin ?: "ROYAL_GOLD") == skin.id
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) skin.colorPrimary else DarkNavyElevated,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                onUpdateProfile(
                                    wallet?.playerName ?: "CashPlayer",
                                    wallet?.playerAvatarId ?: 0,
                                    skin.id
                                )
                            }
                            .testTag("dice_skin_${skin.id}"),
                        color = if (isSelected) skin.colorPrimary.copy(alpha = 0.12f) else DarkNavyCard
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(skin.colorPrimary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = skin.iconEmoji, fontSize = 20.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = skin.name, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = skin.description, color = TextMuted, fontSize = 10.sp)
                                }
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Equipped",
                                    tint = skin.colorPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Sound & Haptics Toggles
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(14.dp)),
                color = DarkNavyCard
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Game Sound Effects", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Switch(
                            checked = soundEnabled,
                            onCheckedChange = { soundEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = GoldPrimary, checkedTrackColor = DarkNavyElevated)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Vibration & Haptics", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Switch(
                            checked = hapticsEnabled,
                            onCheckedChange = { hapticsEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = CashGreen, checkedTrackColor = DarkNavyElevated)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Edit Name Dialog
    if (isEditNameDialogVisible) {
        var tempName by remember { mutableStateOf(wallet?.playerName ?: "CashPlayer") }
        Dialog(onDismissRequest = { isEditNameDialogVisible = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.5.dp, GoldPrimary, RoundedCornerShape(20.dp)),
                color = DarkNavySurface
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = "EDIT PLAYER NAME", color = GoldPrimary, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { if (it.length <= 16) tempName = it },
                        label = { Text("Display Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = DarkNavyElevated,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (tempName.isNotBlank()) {
                                onUpdateProfile(
                                    tempName.trim(),
                                    wallet?.playerAvatarId ?: 0,
                                    wallet?.selectedDiceSkin ?: "ROYAL_GOLD"
                                )
                                isEditNameDialogVisible = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color(0xFF0F172A))
                    ) {
                        Text(text = "SAVE NAME", fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun StatBox(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp)),
        color = DarkNavyCard
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = color, fontWeight = FontWeight.Black, fontSize = 18.sp)
        }
    }
}
