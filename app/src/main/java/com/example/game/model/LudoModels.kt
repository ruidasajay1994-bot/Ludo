package com.example.game.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.LudoBlue
import com.example.ui.theme.LudoBlueDark
import com.example.ui.theme.LudoGreen
import com.example.ui.theme.LudoGreenDark
import com.example.ui.theme.LudoRed
import com.example.ui.theme.LudoRedDark
import com.example.ui.theme.LudoYellow
import com.example.ui.theme.LudoYellowDark

enum class LudoColor(
    val title: String,
    val primaryColor: Color,
    val darkColor: Color,
    val startIndexOnTrack: Int,
    val homeEntryIndex: Int
) {
    RED("Red", LudoRed, LudoRedDark, 0, 50),
    GREEN("Green", LudoGreen, LudoGreenDark, 13, 11),
    YELLOW("Yellow", LudoYellow, LudoYellowDark, 26, 24),
    BLUE("Blue", LudoBlue, LudoBlueDark, 39, 37)
}

enum class GameMode(val title: String, val description: String) {
    QUICK_1V1("1v1 Cash Duel", "Fast 2-Player Head-to-Head Battle"),
    CLASSIC_4P("4-Player Mega Pot", "Full 4-Player Battle for Top Prize"),
    SPEED_RUSH("Speed Timer Rush", "Score max points in 4 minutes!"),
    PRACTICE_BOTS("Practice Arena", "Free Practice vs Smart AI")
}

data class ContestTier(
    val id: String,
    val name: String,
    val mode: GameMode,
    val entryFee: Double,
    val prizePool: Double,
    val firstPrize: Double,
    val secondPrize: Double = 0.0,
    val playerCount: Int = 2,
    val isPopular: Boolean = false,
    val bonusAllowed: Double = 0.0
)

data class BoardCoordinate(val row: Int, val col: Int)

data class Pawn(
    val id: Int, // 0..3
    val color: LudoColor,
    val step: Int = -1 // -1 = In Yard, 0..50 = On common track, 51..55 = In Home Run, 56 = Home
) {
    val isYard: Boolean get() = step == -1
    val isHome: Boolean get() = step >= 56
    val isInHomeRun: Boolean get() = step in 51..55
    val isOnTrack: Boolean get() = step in 0..50
}

data class Player(
    val id: String,
    val name: String,
    val color: LudoColor,
    val isHuman: Boolean,
    val pawns: List<Pawn>,
    val score: Int = 0,
    val avatarIndex: Int = 0,
    val consecutiveSixes: Int = 0,
    val isAutoPlay: Boolean = false,
    val cashBalance: Double = 0.0
) {
    val homePawnsCount: Int get() = pawns.count { it.isHome }
    val isFinished: Boolean get() = pawns.all { it.isHome }
}

data class DiceState(
    val value: Int = 6,
    val isRolling: Boolean = false,
    val rollHistory: List<Int> = emptyList()
)

data class ChatMessage(
    val senderName: String,
    val senderColor: LudoColor,
    val message: String,
    val isEmoji: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

object BoardPathUtils {
    // 52 Common Perimeter Track Coordinates
    val COMMON_TRACK = listOf(
        BoardCoordinate(6, 1),   // 0 - Red Start (Star)
        BoardCoordinate(6, 2),   // 1
        BoardCoordinate(6, 3),   // 2
        BoardCoordinate(6, 4),   // 3
        BoardCoordinate(6, 5),   // 4
        BoardCoordinate(5, 6),   // 5
        BoardCoordinate(4, 6),   // 6
        BoardCoordinate(3, 6),   // 7
        BoardCoordinate(2, 6),   // 8 - Safe Star
        BoardCoordinate(1, 6),   // 9
        BoardCoordinate(0, 6),   // 10
        BoardCoordinate(0, 7),   // 11
        BoardCoordinate(0, 8),   // 12
        BoardCoordinate(1, 8),   // 13 - Green Start (Star)
        BoardCoordinate(2, 8),   // 14
        BoardCoordinate(3, 8),   // 15
        BoardCoordinate(4, 8),   // 16
        BoardCoordinate(5, 8),   // 17
        BoardCoordinate(6, 9),   // 18
        BoardCoordinate(6, 10),  // 19
        BoardCoordinate(6, 11),  // 20
        BoardCoordinate(6, 12),  // 21 - Safe Star
        BoardCoordinate(6, 13),  // 22
        BoardCoordinate(6, 14),  // 23
        BoardCoordinate(7, 14),  // 24
        BoardCoordinate(8, 14),  // 25
        BoardCoordinate(8, 13),  // 26 - Yellow Start (Star)
        BoardCoordinate(8, 12),  // 27
        BoardCoordinate(8, 11),  // 28
        BoardCoordinate(8, 10),  // 29
        BoardCoordinate(8, 9),   // 30
        BoardCoordinate(9, 8),   // 31
        BoardCoordinate(10, 8),  // 32
        BoardCoordinate(11, 8),  // 33
        BoardCoordinate(12, 8),  // 34 - Safe Star
        BoardCoordinate(13, 8),  // 35
        BoardCoordinate(14, 8),  // 36
        BoardCoordinate(14, 7),  // 37
        BoardCoordinate(14, 6),  // 38
        BoardCoordinate(13, 6),  // 39 - Blue Start (Star)
        BoardCoordinate(12, 6),  // 40
        BoardCoordinate(11, 6),  // 41
        BoardCoordinate(10, 6),  // 42
        BoardCoordinate(9, 6),   // 43
        BoardCoordinate(8, 5),   // 44
        BoardCoordinate(8, 4),   // 45
        BoardCoordinate(8, 3),   // 46
        BoardCoordinate(8, 2),   // 47 - Safe Star
        BoardCoordinate(8, 1),   // 48
        BoardCoordinate(8, 0),   // 49
        BoardCoordinate(7, 0),   // 50
        BoardCoordinate(6, 0)    // 51
    )

    // Safe Star positions on the 52-tile track
    val SAFE_STARS = setOf(0, 8, 13, 21, 26, 34, 39, 47)

    // Home runs for each color (5 steps each)
    val RED_HOME_RUN = listOf(
        BoardCoordinate(7, 1),
        BoardCoordinate(7, 2),
        BoardCoordinate(7, 3),
        BoardCoordinate(7, 4),
        BoardCoordinate(7, 5)
    )

    val GREEN_HOME_RUN = listOf(
        BoardCoordinate(1, 7),
        BoardCoordinate(2, 7),
        BoardCoordinate(3, 7),
        BoardCoordinate(4, 7),
        BoardCoordinate(5, 7)
    )

    val YELLOW_HOME_RUN = listOf(
        BoardCoordinate(7, 13),
        BoardCoordinate(7, 12),
        BoardCoordinate(7, 11),
        BoardCoordinate(7, 10),
        BoardCoordinate(7, 9)
    )

    val BLUE_HOME_RUN = listOf(
        BoardCoordinate(13, 7),
        BoardCoordinate(12, 7),
        BoardCoordinate(11, 7),
        BoardCoordinate(10, 7),
        BoardCoordinate(9, 7)
    )

    // Yard positions (4 pawns per color)
    val YARDS = mapOf(
        LudoColor.RED to listOf(
            BoardCoordinate(2, 2), BoardCoordinate(2, 3),
            BoardCoordinate(3, 2), BoardCoordinate(3, 3)
        ),
        LudoColor.GREEN to listOf(
            BoardCoordinate(2, 11), BoardCoordinate(2, 12),
            BoardCoordinate(3, 11), BoardCoordinate(3, 12)
        ),
        LudoColor.YELLOW to listOf(
            BoardCoordinate(11, 11), BoardCoordinate(11, 12),
            BoardCoordinate(12, 11), BoardCoordinate(12, 12)
        ),
        LudoColor.BLUE to listOf(
            BoardCoordinate(11, 2), BoardCoordinate(11, 3),
            BoardCoordinate(12, 2), BoardCoordinate(12, 3)
        )
    )

    val HOME_CENTER = mapOf(
        LudoColor.RED to BoardCoordinate(7, 6),
        LudoColor.GREEN to BoardCoordinate(6, 7),
        LudoColor.YELLOW to BoardCoordinate(7, 8),
        LudoColor.BLUE to BoardCoordinate(8, 7)
    )

    fun getPawnCoordinate(pawn: Pawn): BoardCoordinate {
        if (pawn.isYard) {
            val list = YARDS[pawn.color] ?: emptyList()
            return list.getOrElse(pawn.id.coerceIn(0, 3)) { BoardCoordinate(0, 0) }
        }
        if (pawn.isHome) {
            return HOME_CENTER[pawn.color] ?: BoardCoordinate(7, 7)
        }
        if (pawn.isInHomeRun) {
            val runIdx = (pawn.step - 51).coerceIn(0, 4)
            return when (pawn.color) {
                LudoColor.RED -> RED_HOME_RUN[runIdx]
                LudoColor.GREEN -> GREEN_HOME_RUN[runIdx]
                LudoColor.YELLOW -> YELLOW_HOME_RUN[runIdx]
                LudoColor.BLUE -> BLUE_HOME_RUN[runIdx]
            }
        }
        // On Common Track: step 0..50 relative to color
        val globalIndex = (pawn.color.startIndexOnTrack + pawn.step) % 52
        return COMMON_TRACK[globalIndex]
    }

    fun isPawnOnSafeSpot(pawn: Pawn): Boolean {
        if (pawn.isYard || pawn.isHome || pawn.isInHomeRun) return true
        val globalIndex = (pawn.color.startIndexOnTrack + pawn.step) % 52
        return SAFE_STARS.contains(globalIndex)
    }

    fun getGlobalTrackIndex(pawn: Pawn): Int? {
        if (!pawn.isOnTrack) return null
        return (pawn.color.startIndexOnTrack + pawn.step) % 52
    }
}
