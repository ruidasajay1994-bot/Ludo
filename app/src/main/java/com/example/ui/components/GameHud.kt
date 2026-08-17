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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.game.model.LudoColor
import com.example.game.model.Player
import com.example.ui.theme.CashGreen
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

@Composable
fun GameTopBar(
    contest: ContestTier?,
    matchTimeRemaining: Int,
    onBackClick: () -> Unit,
    onChatClick: () -> Unit,
    onForfeitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val minutes = matchTimeRemaining / 60
    val seconds = matchTimeRemaining % 60
    val timeFormatted = "%02d:%02d".format(minutes, seconds)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp)
            .testTag("game_top_bar"),
        color = DarkNavySurface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("game_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Leave Match",
                        tint = TextWhite
                    )
                }

                // Prize Pool Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(listOf(GoldDark, GoldPrimary))
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Prize",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (contest != null && contest.prizePool > 0) "Prize: ₹${"%.2f".format(contest.prizePool)}" else "Practice Mode",
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Match Timer
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkNavyElevated)
                    .border(1.dp, if (matchTimeRemaining < 60) LudoRed else GoldSecondary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Timer",
                        tint = if (matchTimeRemaining < 60) LudoRed else GoldPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = timeFormatted,
                        color = if (matchTimeRemaining < 60) LudoRed else TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            // Chat & Forfeit Buttons
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onChatClick,
                    modifier = Modifier.testTag("game_chat_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubble,
                        contentDescription = "Quick Chat",
                        tint = GoldPrimary
                    )
                }
                IconButton(
                    onClick = onForfeitClick,
                    modifier = Modifier.testTag("game_forfeit_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = "Forfeit",
                        tint = LudoRed
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerHudCard(
    player: Player,
    isCurrentTurn: Boolean,
    turnSecondsRemaining: Int,
    chatMessage: String?,
    modifier: Modifier = Modifier
) {
    val progress = (turnSecondsRemaining / 15f).coerceIn(0f, 1f)

    Column(
        modifier = modifier.testTag("player_hud_${player.id}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Chat Bubble popup if active
        AnimatedVisibility(
            visible = chatMessage != null,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 }
        ) {
            Box(
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = chatMessage ?: "",
                    color = Color(0xFF0F172A),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = if (isCurrentTurn) 2.dp else 1.dp,
                    color = if (isCurrentTurn) player.color.primaryColor else DarkNavyElevated,
                    shape = RoundedCornerShape(12.dp)
                ),
            color = if (isCurrentTurn) DarkNavyCard else DarkNavySurface
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar with Circular Turn Progress Ring
                Box(
                    modifier = Modifier.size(36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCurrentTurn) {
                        CircularProgressIndicator(
                            progress = { progress },
                            color = player.color.primaryColor,
                            trackColor = DarkNavyElevated,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(player.color.darkColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (player.isHuman) "👤" else "🤖",
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                Column {
                    Text(
                        text = player.name.take(10),
                        color = if (isCurrentTurn) TextWhite else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = if (isCurrentTurn) FontWeight.Bold else FontWeight.Normal
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Score: ${player.score}",
                            color = GoldPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "🏠 ${player.homePawnsCount}/4",
                            color = CashGreen,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
