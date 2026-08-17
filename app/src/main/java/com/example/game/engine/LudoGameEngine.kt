package com.example.game.engine

import com.example.game.model.BoardPathUtils
import com.example.game.model.ContestTier
import com.example.game.model.DiceState
import com.example.game.model.GameMode
import com.example.game.model.LudoColor
import com.example.game.model.Pawn
import com.example.game.model.Player
import kotlin.random.Random

data class MoveResult(
    val movedPawn: Pawn,
    val capturedPawns: List<Pawn>,
    val pointsAwarded: Int,
    val getsBonusTurn: Boolean,
    val reachedHome: Boolean
)

class LudoGameEngine {

    companion object {
        fun createInitialPlayers(
            mode: GameMode,
            humanName: String,
            humanAvatar: Int
        ): List<Player> {
            val human = Player(
                id = "human_1",
                name = humanName,
                color = LudoColor.RED,
                isHuman = true,
                avatarIndex = humanAvatar,
                pawns = (0..3).map { Pawn(id = it, color = LudoColor.RED, step = if (mode == GameMode.SPEED_RUSH) 0 else -1) }
            )

            val botProfiles = listOf(
                Pair("LudoPro_Rohit", 1),
                Pair("CashQueen_Amy", 2),
                Pair("AceStriker_Dev", 3),
                Pair("JackpotKing", 4)
            )

            return when (mode) {
                GameMode.QUICK_1V1 -> {
                    val botYellow = Player(
                        id = "bot_yellow",
                        name = botProfiles[0].first,
                        color = LudoColor.YELLOW,
                        isHuman = false,
                        avatarIndex = botProfiles[0].second,
                        pawns = (0..3).map { Pawn(id = it, color = LudoColor.YELLOW, step = -1) }
                    )
                    listOf(human, botYellow)
                }
                GameMode.CLASSIC_4P, GameMode.SPEED_RUSH, GameMode.PRACTICE_BOTS -> {
                    val botGreen = Player(
                        id = "bot_green",
                        name = botProfiles[0].first,
                        color = LudoColor.GREEN,
                        isHuman = false,
                        avatarIndex = botProfiles[0].second,
                        pawns = (0..3).map { Pawn(id = it, color = LudoColor.GREEN, step = if (mode == GameMode.SPEED_RUSH) 0 else -1) }
                    )
                    val botYellow = Player(
                        id = "bot_yellow",
                        name = botProfiles[1].first,
                        color = LudoColor.YELLOW,
                        isHuman = false,
                        avatarIndex = botProfiles[1].second,
                        pawns = (0..3).map { Pawn(id = it, color = LudoColor.YELLOW, step = if (mode == GameMode.SPEED_RUSH) 0 else -1) }
                    )
                    val botBlue = Player(
                        id = "bot_blue",
                        name = botProfiles[2].first,
                        color = LudoColor.BLUE,
                        isHuman = false,
                        avatarIndex = botProfiles[2].second,
                        pawns = (0..3).map { Pawn(id = it, color = LudoColor.BLUE, step = if (mode == GameMode.SPEED_RUSH) 0 else -1) }
                    )
                    listOf(human, botGreen, botYellow, botBlue)
                }
            }
        }

        fun getMovablePawns(player: Player, dice: Int): List<Pawn> {
            return player.pawns.filter { pawn ->
                canMovePawn(pawn, dice)
            }
        }

        fun canMovePawn(pawn: Pawn, dice: Int): Boolean {
            if (pawn.isHome) return false
            if (pawn.isYard) {
                return dice == 6
            }
            // On board or in home run
            return (pawn.step + dice) <= 56
        }

        fun executePawnMove(
            movingPlayer: Player,
            allPlayers: List<Player>,
            pawn: Pawn,
            dice: Int
        ): Pair<List<Player>, MoveResult> {
            var bonusTurn = (dice == 6)
            var reachedHome = false
            var points = dice
            val capturedList = mutableListOf<Pawn>()

            val updatedStep = if (pawn.isYard) {
                if (dice == 6) 0 else -1
            } else {
                pawn.step + dice
            }

            val updatedPawn = pawn.copy(step = updatedStep)
            if (updatedStep == 56) {
                reachedHome = true
                points += 56
                bonusTurn = true
            }

            // Check captures on track (only if not on safe star)
            val updatedPlayers = allPlayers.map { player ->
                if (player.id == movingPlayer.id) {
                    val newPawns = player.pawns.map { if (it.id == pawn.id) updatedPawn else it }
                    val newScore = player.score + points
                    player.copy(pawns = newPawns, score = newScore)
                } else {
                    // Check if any opponent pawn is at the same global coordinate and not safe
                    val newPawns = player.pawns.map { oppPawn ->
                        if (oppPawn.isOnTrack && updatedPawn.isOnTrack) {
                            val myGlobal = BoardPathUtils.getGlobalTrackIndex(updatedPawn)
                            val oppGlobal = BoardPathUtils.getGlobalTrackIndex(oppPawn)
                            if (myGlobal != null && myGlobal == oppGlobal && !BoardPathUtils.isPawnOnSafeSpot(updatedPawn)) {
                                // Captured!
                                capturedList.add(oppPawn)
                                oppPawn.copy(step = -1) // back to yard
                            } else {
                                oppPawn
                            }
                        } else {
                            oppPawn
                        }
                    }
                    player.copy(pawns = newPawns)
                }
            }

            if (capturedList.isNotEmpty()) {
                points += 20 * capturedList.size
                bonusTurn = true
            }

            // Re-apply capture bonus points to moving player
            val finalPlayers = updatedPlayers.map { player ->
                if (player.id == movingPlayer.id && capturedList.isNotEmpty()) {
                    player.copy(score = player.score + (20 * capturedList.size))
                } else {
                    player
                }
            }

            val moveResult = MoveResult(
                movedPawn = updatedPawn,
                capturedPawns = capturedList,
                pointsAwarded = points,
                getsBonusTurn = bonusTurn,
                reachedHome = reachedHome
            )

            return Pair(finalPlayers, moveResult)
        }

        fun chooseBestBotMove(
            botPlayer: Player,
            allPlayers: List<Player>,
            dice: Int
        ): Pawn? {
            val movables = getMovablePawns(botPlayer, dice)
            if (movables.isEmpty()) return null
            if (movables.size == 1) return movables.first()

            // 1. Capture opportunity
            for (p in movables) {
                val nextStep = if (p.isYard) 0 else p.step + dice
                val testPawn = p.copy(step = nextStep)
                if (testPawn.isOnTrack && !BoardPathUtils.isPawnOnSafeSpot(testPawn)) {
                    val myGlobal = BoardPathUtils.getGlobalTrackIndex(testPawn)
                    val hasTarget = allPlayers.filter { it.id != botPlayer.id }.any { opp ->
                        opp.pawns.any { oppP ->
                            oppP.isOnTrack && BoardPathUtils.getGlobalTrackIndex(oppP) == myGlobal
                        }
                    }
                    if (hasTarget) return p
                }
            }

            // 2. Reach Home (step 56)
            val homePawn = movables.find { it.step + dice == 56 }
            if (homePawn != null) return homePawn

            // 3. Move into Home Run (step > 50)
            val intoHomeRun = movables.find { it.step <= 50 && it.step + dice > 50 }
            if (intoHomeRun != null) return intoHomeRun

            // 4. Move to a Safe Star spot
            for (p in movables) {
                val nextStep = if (p.isYard) 0 else p.step + dice
                val testPawn = p.copy(step = nextStep)
                if (BoardPathUtils.isPawnOnSafeSpot(testPawn)) return p
            }

            // 5. Open from Yard if 6
            if (dice == 6) {
                val yardPawn = movables.find { it.isYard }
                if (yardPawn != null) return yardPawn
            }

            // 6. Move farthest advanced pawn
            return movables.maxByOrNull { it.step }
        }
    }
}
