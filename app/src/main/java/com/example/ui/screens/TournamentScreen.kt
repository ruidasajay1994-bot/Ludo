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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.game.model.ContestTier
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.CashGreen
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavyElevated
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.LudoRed
import com.example.ui.theme.TextGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val totalWon: Double,
    val winRate: Int,
    val avatarEmoji: String
)

@Composable
fun TournamentScreen(
    onJoinTournament: () -> Unit,
    modifier: Modifier = Modifier
) {
    val topPlayers = listOf(
        LeaderboardEntry(1, "LudoKing_Rohit", 4850.0, 84, "👑"),
        LeaderboardEntry(2, "CashQueen_Amy", 3920.0, 79, "💎"),
        LeaderboardEntry(3, "AceStriker_Dev", 2840.0, 76, "🔥"),
        LeaderboardEntry(4, "JackpotMaster", 2150.0, 71, "⚡"),
        LeaderboardEntry(5, "SpeedDemon_99", 1890.0, 68, "🎯"),
        LeaderboardEntry(6, "LuckyRoller", 1420.0, 65, "🎲"),
        LeaderboardEntry(7, "You (CashMaster)", 680.0, 75, "👤")
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .testTag("tournament_screen"),
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
                        text = "🏆 MEGA CASH LEAGUES",
                        color = GoldPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // 2. Grand Tournament Banner
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
                                listOf(Color(0xFF2E1065), Color(0xFF1E1B4B), DarkNavyCard)
                            )
                        )
                        .border(1.5.dp, GoldPrimary, RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(GoldPrimary)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "WEEKLY GRAND CHAMPIONSHIP",
                                    color = Color(0xFF0F172A),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp
                                )
                            }
                            Text(text = "Ends in 2d 14h", color = CashGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "₹1,00,000 GUARANTEED", color = TextWhite, fontSize = 24.sp, fontWeight = FontWeight.Black)
                        Text(text = "Top 50 Players Win Real Cash Prizes", color = TextMuted, fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onJoinTournament,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("join_tournament_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color(0xFF0F172A))
                        ) {
                            Text(text = "ENTER LEAGUE (₹50 ENTRY) 🎲", fontWeight = FontWeight.Black, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // 3. Top 3 Podium Cards
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Rank 2
                PodiumCard(topPlayers[1], modifier = Modifier.weight(1f))
                // Rank 1
                PodiumCard(topPlayers[0], isFirst = true, modifier = Modifier.weight(1.15f))
                // Rank 3
                PodiumCard(topPlayers[2], modifier = Modifier.weight(1f))
            }
        }

        // 4. Full Leaderboard Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🌟 GLOBAL CASH LEADERBOARD",
                    color = TextGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(text = "Live Ranks", color = CashGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // 5. Leaderboard Items
        items(topPlayers) { player ->
            LeaderboardRow(
                entry = player,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PodiumCard(
    entry: LeaderboardEntry,
    isFirst: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .shadow(if (isFirst) 10.dp else 4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .border(if (isFirst) 1.5.dp else 1.dp, if (isFirst) GoldPrimary else DarkNavyElevated, RoundedCornerShape(16.dp)),
        color = if (isFirst) DarkNavyElevated else DarkNavyCard
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = entry.avatarEmoji, fontSize = if (isFirst) 32.sp else 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "#${entry.rank}",
                color = if (isFirst) GoldPrimary else TextMuted,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp
            )
            Text(
                text = entry.name.take(8),
                color = TextWhite,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "₹${entry.totalWon.toInt()}",
                color = CashGreen,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun LeaderboardRow(
    entry: LeaderboardEntry,
    modifier: Modifier = Modifier
) {
    val isYou = entry.name.contains("You")

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, if (isYou) GoldPrimary else DarkNavyElevated, RoundedCornerShape(12.dp)),
        color = if (isYou) GoldPrimary.copy(alpha = 0.12f) else DarkNavyCard
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (entry.rank <= 3) GoldPrimary else DarkNavyElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${entry.rank}",
                        color = if (entry.rank <= 3) Color(0xFF0F172A) else TextWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(text = entry.avatarEmoji, fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = entry.name,
                        color = if (isYou) GoldPrimary else TextWhite,
                        fontWeight = if (isYou) FontWeight.Black else FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(text = "Win Rate: ${entry.winRate}%", color = TextMuted, fontSize = 10.sp)
                }
            }

            Text(
                text = "₹${"%.2f".format(entry.totalWon)}",
                color = CashGreen,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
            )
        }
    }
}
