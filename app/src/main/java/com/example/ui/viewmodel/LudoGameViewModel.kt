package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entities.UserWalletEntity
import com.example.data.local.repository.CashLudoRepository
import com.example.game.engine.LudoGameEngine
import com.example.game.model.ChatMessage
import com.example.game.model.ContestTier
import com.example.game.model.DiceState
import com.example.game.model.GameMode
import com.example.game.model.LudoColor
import com.example.game.model.Pawn
import com.example.game.model.Player
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class GamePhase {
    IDLE,
    WAITING_ROLL,
    ROLLING,
    SELECTING_PAWN,
    PAWN_MOVING,
    BOT_THINKING,
    GAME_OVER
}

data class GameUiState(
    val currentContest: ContestTier? = null,
    val players: List<Player> = emptyList(),
    val currentTurnIndex: Int = 0,
    val phase: GamePhase = GamePhase.IDLE,
    val diceState: DiceState = DiceState(),
    val movablePawnIds: Set<Int> = emptySet(),
    val consecutiveSixes: Int = 0,
    val turnTimeRemaining: Int = 15,
    val matchTimeRemaining: Int = 300, // 5 minutes
    val winner: Player? = null,
    val isTie: Boolean = false,
    val celebrationEvent: String? = null,
    val chatMessages: List<ChatMessage> = emptyList(),
    val liveAnnouncement: String = "Roll the dice to start!",
    val activeChatBubble: Pair<String, String>? = null // PlayerId to Message
) {
    val currentPlayer: Player? get() = players.getOrNull(currentTurnIndex)
    val isHumanTurn: Boolean get() = currentPlayer?.isHuman == true
}

class LudoGameViewModel(
    private val repository: CashLudoRepository
) : ViewModel() {

    val walletState: StateFlow<UserWalletEntity?> = repository.walletFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _gameState = MutableStateFlow(GameUiState())
    val gameState: StateFlow<GameUiState> = _gameState.asStateFlow()

    private val _gameEvents = MutableSharedFlow<String>()
    val gameEvents: SharedFlow<String> = _gameEvents.asSharedFlow()

    private var turnTimerJob: Job? = null
    private var matchTimerJob: Job? = null
    private var botJob: Job? = null

    val contestTiers = listOf(
        ContestTier(
            id = "tier_1v1_10",
            name = "Bronze 1v1 Battle",
            mode = GameMode.QUICK_1V1,
            entryFee = 10.0,
            prizePool = 18.0,
            firstPrize = 18.0,
            playerCount = 2,
            isPopular = false,
            bonusAllowed = 2.0
        ),
        ContestTier(
            id = "tier_1v1_25",
            name = "Silver 1v1 Duel",
            mode = GameMode.QUICK_1V1,
            entryFee = 25.0,
            prizePool = 45.0,
            firstPrize = 45.0,
            playerCount = 2,
            isPopular = true,
            bonusAllowed = 5.0
        ),
        ContestTier(
            id = "tier_1v1_50",
            name = "Gold 1v1 Cash Pot",
            mode = GameMode.QUICK_1V1,
            entryFee = 50.0,
            prizePool = 90.0,
            firstPrize = 90.0,
            playerCount = 2,
            isPopular = false,
            bonusAllowed = 10.0
        ),
        ContestTier(
            id = "tier_1v1_100",
            name = "Diamond 1v1 High Roller",
            mode = GameMode.QUICK_1V1,
            entryFee = 100.0,
            prizePool = 180.0,
            firstPrize = 180.0,
            playerCount = 2,
            isPopular = false,
            bonusAllowed = 20.0
        ),
        ContestTier(
            id = "tier_4p_25",
            name = "Mega 4-Player Cash Pot",
            mode = GameMode.CLASSIC_4P,
            entryFee = 25.0,
            prizePool = 85.0,
            firstPrize = 60.0,
            secondPrize = 25.0,
            playerCount = 4,
            isPopular = true,
            bonusAllowed = 5.0
        ),
        ContestTier(
            id = "tier_4p_50",
            name = "Grand 4P Championship",
            mode = GameMode.CLASSIC_4P,
            entryFee = 50.0,
            prizePool = 175.0,
            firstPrize = 125.0,
            secondPrize = 50.0,
            playerCount = 4,
            isPopular = false,
            bonusAllowed = 10.0
        ),
        ContestTier(
            id = "tier_speed_25",
            name = "Speed Rush 4-Min Cash",
            mode = GameMode.SPEED_RUSH,
            entryFee = 25.0,
            prizePool = 45.0,
            firstPrize = 45.0,
            playerCount = 2,
            isPopular = true,
            bonusAllowed = 5.0
        ),
        ContestTier(
            id = "tier_practice",
            name = "Free Practice Arena",
            mode = GameMode.PRACTICE_BOTS,
            entryFee = 0.0,
            prizePool = 0.0,
            firstPrize = 0.0,
            playerCount = 4,
            isPopular = false,
            bonusAllowed = 0.0
        )
    )

    fun startContest(tier: ContestTier, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            if (tier.entryFee > 0) {
                val deducted = repository.deductEntryFee(tier.entryFee)
                if (!deducted) {
                    onError("Insufficient balance! Add cash to join this contest.")
                    return@launch
                }
            }

            val userWallet = repository.getWallet()
            val initialPlayers = LudoGameEngine.createInitialPlayers(
                mode = tier.mode,
                humanName = userWallet.playerName,
                humanAvatar = userWallet.playerAvatarId
            )

            _gameState.value = GameUiState(
                currentContest = tier,
                players = initialPlayers,
                currentTurnIndex = 0,
                phase = GamePhase.WAITING_ROLL,
                diceState = DiceState(value = 6, isRolling = false),
                turnTimeRemaining = 15,
                matchTimeRemaining = if (tier.mode == GameMode.SPEED_RUSH) 240 else 360,
                liveAnnouncement = "Match Started! Your turn to roll."
            )

            startTurnTimer()
            startMatchTimer()
            onSuccess()
        }
    }

    private fun startTurnTimer() {
        turnTimerJob?.cancel()
        turnTimerJob = viewModelScope.launch {
            _gameState.update { it.copy(turnTimeRemaining = 15) }
            while (_gameState.value.turnTimeRemaining > 0 && _gameState.value.phase != GamePhase.GAME_OVER) {
                delay(1000)
                _gameState.update { it.copy(turnTimeRemaining = it.turnTimeRemaining - 1) }
            }
            // Auto timeout handling
            if (_gameState.value.phase == GamePhase.WAITING_ROLL) {
                rollDice()
            } else if (_gameState.value.phase == GamePhase.SELECTING_PAWN) {
                // Auto move first available pawn
                val movable = _gameState.value.movablePawnIds.firstOrNull()
                if (movable != null) {
                    selectPawn(movable)
                } else {
                    passTurn()
                }
            }
        }
    }

    private fun startMatchTimer() {
        matchTimerJob?.cancel()
        matchTimerJob = viewModelScope.launch {
            while (_gameState.value.matchTimeRemaining > 0 && _gameState.value.phase != GamePhase.GAME_OVER) {
                delay(1000)
                _gameState.update { it.copy(matchTimeRemaining = it.matchTimeRemaining - 1) }
            }
            if (_gameState.value.phase != GamePhase.GAME_OVER) {
                endGameByScore()
            }
        }
    }

    fun rollDice() {
        val state = _gameState.value
        val currentPlayer = state.currentPlayer ?: return
        if (state.phase != GamePhase.WAITING_ROLL && state.phase != GamePhase.BOT_THINKING) return

        viewModelScope.launch {
            _gameState.update { it.copy(phase = GamePhase.ROLLING, diceState = it.diceState.copy(isRolling = true)) }
            
            // Dice roll animation frames
            for (i in 1..6) {
                delay(70)
                val tempVal = Random.nextInt(1, 7)
                _gameState.update { it.copy(diceState = it.diceState.copy(value = tempVal)) }
            }

            val finalValue = Random.nextInt(1, 7)
            val newSixes = if (finalValue == 6) state.consecutiveSixes + 1 else 0

            _gameState.update {
                it.copy(
                    phase = GamePhase.SELECTING_PAWN,
                    diceState = DiceState(
                        value = finalValue,
                        isRolling = false,
                        rollHistory = (listOf(finalValue) + it.diceState.rollHistory).take(10)
                    ),
                    consecutiveSixes = newSixes
                )
            }

            // Check 3 consecutive 6s forfeiture rule
            if (newSixes >= 3) {
                _gameState.update {
                    it.copy(liveAnnouncement = "${currentPlayer.name} rolled three 6s in a row! Turn forfeited.")
                }
                delay(1200)
                passTurn()
                return@launch
            }

            val movables = LudoGameEngine.getMovablePawns(currentPlayer, finalValue)
            if (movables.isEmpty()) {
                _gameState.update {
                    it.copy(
                        movablePawnIds = emptySet(),
                        liveAnnouncement = "${currentPlayer.name} rolled $finalValue. No moves available."
                    )
                }
                delay(1000)
                passTurn()
            } else if (movables.size == 1 && !currentPlayer.isHuman) {
                _gameState.update {
                    it.copy(
                        movablePawnIds = movables.map { p -> p.id }.toSet(),
                        liveAnnouncement = "${currentPlayer.name} rolled $finalValue."
                    )
                }
                delay(500)
                selectPawn(movables.first().id)
            } else if (movables.size == 1 && currentPlayer.isHuman) {
                // Auto move for human if only 1 move is possible for seamless fast flow
                _gameState.update {
                    it.copy(
                        movablePawnIds = movables.map { p -> p.id }.toSet(),
                        liveAnnouncement = "Rolled $finalValue! Moving token..."
                    )
                }
                delay(300)
                selectPawn(movables.first().id)
            } else {
                _gameState.update {
                    it.copy(
                        movablePawnIds = movables.map { p -> p.id }.toSet(),
                        liveAnnouncement = if (currentPlayer.isHuman) "Rolled $finalValue! Choose a pawn to move." else "${currentPlayer.name} is choosing a move..."
                    )
                }
                if (!currentPlayer.isHuman) {
                    delay(700)
                    val chosen = LudoGameEngine.chooseBestBotMove(currentPlayer, state.players, finalValue)
                    if (chosen != null) {
                        selectPawn(chosen.id)
                    } else {
                        passTurn()
                    }
                }
            }
        }
    }

    fun selectPawn(pawnId: Int) {
        val state = _gameState.value
        val currentPlayer = state.currentPlayer ?: return
        val targetPawn = currentPlayer.pawns.find { it.id == pawnId } ?: return
        val dice = state.diceState.value

        if (!LudoGameEngine.canMovePawn(targetPawn, dice)) return

        viewModelScope.launch {
            _gameState.update { it.copy(phase = GamePhase.PAWN_MOVING, movablePawnIds = emptySet()) }

            val (updatedPlayers, result) = LudoGameEngine.executePawnMove(
                movingPlayer = currentPlayer,
                allPlayers = state.players,
                pawn = targetPawn,
                dice = dice
            )

            var celebrationText: String? = null
            if (result.capturedPawns.isNotEmpty()) {
                celebrationText = "💥 KNOCKOUT! +${20 * result.capturedPawns.size} PTS & BONUS ROLL!"
                _gameEvents.emit("CAPTURE")
            } else if (result.reachedHome) {
                celebrationText = "🏆 TOKEN HOME! +56 PTS!"
                _gameEvents.emit("HOME")
            }

            _gameState.update {
                it.copy(
                    players = updatedPlayers,
                    celebrationEvent = celebrationText,
                    liveAnnouncement = when {
                        result.capturedPawns.isNotEmpty() -> "${currentPlayer.name} knocked out opponent token! Bonus turn."
                        result.reachedHome -> "${currentPlayer.name}'s pawn reached HOME! +56 pts."
                        result.getsBonusTurn -> "${currentPlayer.name} rolled 6 and gets another roll!"
                        else -> "${currentPlayer.name} moved pawn ${result.movedPawn.id + 1}."
                    }
                )
            }

            delay(600)
            if (celebrationText != null) {
                delay(800)
                _gameState.update { it.copy(celebrationEvent = null) }
            }

            // Check Win condition (All pawns home or points cap)
            val hasWon = updatedPlayers.find { it.id == currentPlayer.id }?.isFinished == true
            if (hasWon) {
                handleGameOver(winner = currentPlayer)
                return@launch
            }

            if (result.getsBonusTurn) {
                _gameState.update {
                    it.copy(
                        phase = if (currentPlayer.isHuman) GamePhase.WAITING_ROLL else GamePhase.BOT_THINKING,
                        liveAnnouncement = "${currentPlayer.name}'s Bonus Roll!"
                    )
                }
                startTurnTimer()
                if (!currentPlayer.isHuman) {
                    delay(800)
                    rollDice()
                }
            } else {
                passTurn()
            }
        }
    }

    private fun passTurn() {
        val state = _gameState.value
        if (state.phase == GamePhase.GAME_OVER) return

        val nextIndex = (state.currentTurnIndex + 1) % state.players.size
        val nextPlayer = state.players[nextIndex]

        _gameState.update {
            it.copy(
                currentTurnIndex = nextIndex,
                phase = if (nextPlayer.isHuman) GamePhase.WAITING_ROLL else GamePhase.BOT_THINKING,
                movablePawnIds = emptySet(),
                consecutiveSixes = 0,
                liveAnnouncement = if (nextPlayer.isHuman) "Your turn! Roll the dice." else "${nextPlayer.name}'s turn..."
            )
        }

        startTurnTimer()

        if (!nextPlayer.isHuman) {
            botJob?.cancel()
            botJob = viewModelScope.launch {
                delay(Random.nextLong(900, 1500))
                rollDice()
            }
        }
    }

    private fun endGameByScore() {
        val state = _gameState.value
        val winner = state.players.maxByOrNull { it.score }
        handleGameOver(winner = winner)
    }

    private fun handleGameOver(winner: Player?) {
        turnTimerJob?.cancel()
        matchTimerJob?.cancel()
        botJob?.cancel()

        val state = _gameState.value
        val contest = state.currentContest ?: return

        val isHumanWinner = winner?.isHuman == true
        val humanPlayer = state.players.find { it.isHuman }
        val rank = if (isHumanWinner) 1 else 2
        val prize = if (isHumanWinner) contest.firstPrize else if (contest.playerCount == 4 && rank == 2) contest.secondPrize else 0.0

        _gameState.update {
            it.copy(
                phase = GamePhase.GAME_OVER,
                winner = winner,
                liveAnnouncement = if (isHumanWinner) "🎉 VICTORY! You won ₹${"%.2f".format(prize)} Cash!" else "Match Ended. Winner: ${winner?.name ?: "Draw"}"
            )
        }

        viewModelScope.launch {
            repository.creditWinnings(
                prize = prize,
                mode = contest.mode.title,
                isWin = isHumanWinner,
                rank = rank,
                playerScore = humanPlayer?.score ?: 0,
                opponentName = state.players.find { !it.isHuman }?.name ?: "Opponent",
                entryFee = contest.entryFee
            )
        }
    }

    fun forfeitGame() {
        val state = _gameState.value
        val botWinner = state.players.find { !it.isHuman }
        handleGameOver(winner = botWinner)
    }

    fun sendChatMessage(msg: String, isEmoji: Boolean = false) {
        val human = _gameState.value.players.find { it.isHuman } ?: return
        val chat = ChatMessage(
            senderName = human.name,
            senderColor = human.color,
            message = msg,
            isEmoji = isEmoji
        )
        _gameState.update {
            it.copy(
                chatMessages = (it.chatMessages + chat).takeLast(20),
                activeChatBubble = Pair(human.id, msg)
            )
        }
        viewModelScope.launch {
            delay(2500)
            _gameState.update { it.copy(activeChatBubble = null) }
            // Bot auto reply sometimes
            if (Random.nextBoolean()) {
                delay(1200)
                val bot = _gameState.value.players.filter { !it.isHuman }.randomOrNull() ?: return@launch
                val botReplies = listOf("Nice move!", "Let's go!", "Good luck!", "🔥", "😎", "Rolling six now!", "GG!")
                val reply = botReplies.random()
                val botChat = ChatMessage(bot.name, bot.color, reply, reply.length <= 2)
                _gameState.update {
                    it.copy(
                        chatMessages = (it.chatMessages + botChat).takeLast(20),
                        activeChatBubble = Pair(bot.id, reply)
                    )
                }
                delay(2500)
                _gameState.update { it.copy(activeChatBubble = null) }
            }
        }
    }

    fun resetGameToIdle() {
        turnTimerJob?.cancel()
        matchTimerJob?.cancel()
        botJob?.cancel()
        _gameState.value = GameUiState()
    }
}
