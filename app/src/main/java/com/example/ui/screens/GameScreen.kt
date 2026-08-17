package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.model.GameMode
import com.example.game.model.LudoColor
import com.example.ui.components.DiceRoller
import com.example.ui.components.GameTopBar
import com.example.ui.components.LudoBoardView
import com.example.ui.components.PlayerHudCard
import com.example.ui.components.QuickChatDialog
import com.example.ui.components.VictoryCashoutDialog
import com.example.ui.theme.CashGreen
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
import com.example.ui.viewmodel.GamePhase
import com.example.ui.viewmodel.GameUiState

@Composable
fun GameScreen(
    gameState: GameUiState,
    onRollDice: () -> Unit,
    onSelectPawn: (Int) -> Unit,
    onSendChatMessage: (String, Boolean) -> Unit,
    onForfeitGame: () -> Unit,
    onPlayAgain: () -> Unit,
    onBackToLobby: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showChatDialog by remember { mutableStateOf(false) }
    var showForfeitConfirm by remember { mutableStateOf(false) }

    val humanPlayer = gameState.players.find { it.isHuman }
    val opponentPlayers = gameState.players.filter { !it.isHuman }

    val canRoll = gameState.isHumanTurn && gameState.phase == GamePhase.WAITING_ROLL

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .testTag("game_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Top HUD Status Bar
            GameTopBar(
                contest = gameState.currentContest,
                matchTimeRemaining = gameState.matchTimeRemaining,
                onBackClick = { showForfeitConfirm = true },
                onChatClick = { showChatDialog = true },
                onForfeitClick = { showForfeitConfirm = true }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Opponent Player Cards Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                opponentPlayers.forEach { opp ->
                    val isOppTurn = (gameState.currentPlayer?.id == opp.id)
                    val chatMsg = if (gameState.activeChatBubble?.first == opp.id) gameState.activeChatBubble?.second else null
                    PlayerHudCard(
                        player = opp,
                        isCurrentTurn = isOppTurn,
                        turnSecondsRemaining = if (isOppTurn) gameState.turnTimeRemaining else 0,
                        chatMessage = chatMsg,
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Main Ludo Board
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                LudoBoardView(
                    players = gameState.players,
                    currentPlayerColor = gameState.currentPlayer?.color,
                    movablePawnIds = gameState.movablePawnIds,
                    isHumanTurn = gameState.isHumanTurn,
                    onPawnSelected = onSelectPawn,
                    modifier = Modifier.fillMaxWidth()
                )

                // Knockout / Home Event Overlay Toast
                if (gameState.celebrationEvent != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .shadow(16.dp, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(listOf(GoldDark, GoldPrimary, LudoRed))
                            )
                            .border(2.dp, Color.White, RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = gameState.celebrationEvent ?: "",
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4. Human Player HUD Card
            if (humanPlayer != null) {
                val isMyTurn = gameState.isHumanTurn
                val myChat = if (gameState.activeChatBubble?.first == humanPlayer.id) gameState.activeChatBubble?.second else null
                PlayerHudCard(
                    player = humanPlayer,
                    isCurrentTurn = isMyTurn,
                    turnSecondsRemaining = if (isMyTurn) gameState.turnTimeRemaining else 0,
                    chatMessage = myChat,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 5. Live Announcement Banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(10.dp)),
                color = DarkNavyCard
            ) {
                Text(
                    text = gameState.liveAnnouncement,
                    color = TextGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 6. Interactive Dice Roller
            DiceRoller(
                diceValue = gameState.diceState.value,
                isRolling = gameState.diceState.isRolling,
                isHumanTurn = gameState.isHumanTurn,
                playerColor = gameState.currentPlayer?.color,
                canRoll = canRoll,
                consecutiveSixes = gameState.consecutiveSixes,
                onRollClick = onRollDice,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Quick Chat Dialog
        if (showChatDialog) {
            QuickChatDialog(
                onDismiss = { showChatDialog = false },
                onSendMessage = onSendChatMessage
            )
        }

        // Forfeit Confirmation Dialog
        if (showForfeitConfirm) {
            AlertDialog(
                onDismissRequest = { showForfeitConfirm = false },
                title = { Text("Leave Match?", color = TextWhite, fontWeight = FontWeight.Bold) },
                text = { Text("Leaving or forfeiting will concede the match to opponents and forfeit the entry fee.", color = TextMuted) },
                confirmButton = {
                    Button(
                        onClick = {
                            showForfeitConfirm = false
                            onForfeitGame()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LudoRed)
                    ) {
                        Text("Forfeit & Leave")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showForfeitConfirm = false }) {
                        Text("Continue Playing", color = TextWhite)
                    }
                },
                containerColor = DarkNavySurface
            )
        }

        // Victory / Game Over Dialog
        if (gameState.phase == GamePhase.GAME_OVER) {
            VictoryCashoutDialog(
                winner = gameState.winner,
                isHumanWinner = gameState.winner?.isHuman == true,
                contest = gameState.currentContest,
                onPlayAgain = onPlayAgain,
                onBackToLobby = onBackToLobby
            )
        }
    }
}
