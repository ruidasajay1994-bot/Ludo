package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.model.BoardCoordinate
import com.example.game.model.BoardPathUtils
import com.example.game.model.LudoColor
import com.example.game.model.Pawn
import com.example.game.model.Player
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.LudoBlue
import com.example.ui.theme.LudoBlueDark
import com.example.ui.theme.LudoGreen
import com.example.ui.theme.LudoGreenDark
import com.example.ui.theme.LudoRed
import com.example.ui.theme.LudoRedDark
import com.example.ui.theme.LudoYellow
import com.example.ui.theme.LudoYellowDark

@Composable
fun LudoBoardView(
    players: List<Player>,
    currentPlayerColor: LudoColor?,
    movablePawnIds: Set<Int>,
    isHumanTurn: Boolean,
    onPawnSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pawnGlow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Surface(
        modifier = modifier
            .aspectRatio(1f)
            .shadow(16.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .border(3.dp, Brush.linearGradient(listOf(GoldPrimary, GoldDark, DarkNavyCard)), RoundedCornerShape(20.dp))
            .testTag("ludo_board_view"),
        color = DarkNavySurface
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val boardSize = maxWidth
            val cellSize = boardSize / 15f

            // 1. Draw Canvas background board grid & paths
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val cellW = w / 15f
                val cellH = h / 15f

                // Fill background
                drawRect(color = Color(0xFF0F172A), size = size)

                // 4 Large Quadrant Yards
                // Top-Left RED Yard (6x6)
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(LudoRed, LudoRedDark),
                        center = Offset(3 * cellW, 3 * cellH),
                        radius = 4 * cellW
                    ),
                    topLeft = Offset(0f, 0f),
                    size = Size(6 * cellW, 6 * cellH)
                )

                // Top-Right GREEN Yard (6x6)
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(LudoGreen, LudoGreenDark),
                        center = Offset(12 * cellW, 3 * cellH),
                        radius = 4 * cellW
                    ),
                    topLeft = Offset(9 * cellW, 0f),
                    size = Size(6 * cellW, 6 * cellH)
                )

                // Bottom-Right YELLOW Yard (6x6)
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(LudoYellow, LudoYellowDark),
                        center = Offset(12 * cellW, 12 * cellH),
                        radius = 4 * cellW
                    ),
                    topLeft = Offset(9 * cellW, 9 * cellH),
                    size = Size(6 * cellW, 6 * cellH)
                )

                // Bottom-Left BLUE Yard (6x6)
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(LudoBlue, LudoBlueDark),
                        center = Offset(3 * cellW, 12 * cellH),
                        radius = 4 * cellW
                    ),
                    topLeft = Offset(0f, 9 * cellW),
                    size = Size(6 * cellW, 6 * cellH)
                )

                // White inner base boxes inside yards
                val yardInnerMargin = 0.8f
                // Red inner base
                drawRoundRect(
                    color = Color(0xFF1E293B),
                    topLeft = Offset(yardInnerMargin * cellW, yardInnerMargin * cellH),
                    size = Size((6 - 2 * yardInnerMargin) * cellW, (6 - 2 * yardInnerMargin) * cellH),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
                )
                // Green inner base
                drawRoundRect(
                    color = Color(0xFF1E293B),
                    topLeft = Offset((9 + yardInnerMargin) * cellW, yardInnerMargin * cellH),
                    size = Size((6 - 2 * yardInnerMargin) * cellW, (6 - 2 * yardInnerMargin) * cellH),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
                )
                // Yellow inner base
                drawRoundRect(
                    color = Color(0xFF1E293B),
                    topLeft = Offset((9 + yardInnerMargin) * cellW, (9 + yardInnerMargin) * cellH),
                    size = Size((6 - 2 * yardInnerMargin) * cellW, (6 - 2 * yardInnerMargin) * cellH),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
                )
                // Blue inner base
                drawRoundRect(
                    color = Color(0xFF1E293B),
                    topLeft = Offset(yardInnerMargin * cellW, (9 + yardInnerMargin) * cellH),
                    size = Size((6 - 2 * yardInnerMargin) * cellW, (6 - 2 * yardInnerMargin) * cellH),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
                )

                // Draw Track Cells (15x15)
                for (r in 0..14) {
                    for (c in 0..14) {
                        // Skip Yard interiors and center 3x3
                        val isYard = (r < 6 && c < 6) || (r < 6 && c >= 9) || (r >= 9 && c < 6) || (r >= 9 && c >= 9)
                        val isCenter = (r in 6..8 && c in 6..8)
                        if (!isYard && !isCenter) {
                            val coord = BoardCoordinate(r, c)
                            val cellColor = when {
                                // Red Home Run (row 7, cols 1..5) & Red Start (row 6, col 1)
                                r == 7 && c in 1..5 -> LudoRed
                                r == 6 && c == 1 -> LudoRed.copy(alpha = 0.85f)
                                // Green Home Run (col 7, rows 1..5) & Green Start (row 1, col 8)
                                c == 7 && r in 1..5 -> LudoGreen
                                r == 1 && c == 8 -> LudoGreen.copy(alpha = 0.85f)
                                // Yellow Home Run (row 7, cols 9..13) & Yellow Start (row 8, col 13)
                                r == 7 && c in 9..13 -> LudoYellow
                                r == 8 && c == 13 -> LudoYellow.copy(alpha = 0.85f)
                                // Blue Home Run (col 7, rows 9..13) & Blue Start (row 13, col 6)
                                c == 7 && r in 9..13 -> LudoBlue
                                r == 13 && c == 6 -> LudoBlue.copy(alpha = 0.85f)
                                else -> Color(0xFF1E293B)
                            }

                            drawRoundRect(
                                color = cellColor,
                                topLeft = Offset(c * cellW + 1f, r * cellH + 1f),
                                size = Size(cellW - 2f, cellH - 2f),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                            )

                            // Subtle cell border
                            drawRoundRect(
                                color = Color(0x33FFFFFF),
                                topLeft = Offset(c * cellW + 1f, r * cellH + 1f),
                                size = Size(cellW - 2f, cellH - 2f),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f),
                                style = Stroke(width = 1f)
                            )
                        }
                    }
                }

                // Center Home Triangles (rows 6..8, cols 6..8)
                val centerLeft = 6 * cellW
                val centerTop = 6 * cellH
                val centerRight = 9 * cellW
                val centerBottom = 9 * cellH
                val centerMidX = 7.5f * cellW
                val centerMidY = 7.5f * cellH

                // Red Triangle (Left)
                val redPath = Path().apply {
                    moveTo(centerLeft, centerTop)
                    lineTo(centerMidX, centerMidY)
                    lineTo(centerLeft, centerBottom)
                    close()
                }
                drawPath(redPath, Brush.radialGradient(listOf(LudoRed, LudoRedDark), Offset(centerLeft, centerMidY), 2 * cellW))

                // Green Triangle (Top)
                val greenPath = Path().apply {
                    moveTo(centerLeft, centerTop)
                    lineTo(centerMidX, centerMidY)
                    lineTo(centerRight, centerTop)
                    close()
                }
                drawPath(greenPath, Brush.radialGradient(listOf(LudoGreen, LudoGreenDark), Offset(centerMidX, centerTop), 2 * cellW))

                // Yellow Triangle (Right)
                val yellowPath = Path().apply {
                    moveTo(centerRight, centerTop)
                    lineTo(centerMidX, centerMidY)
                    lineTo(centerRight, centerBottom)
                    close()
                }
                drawPath(yellowPath, Brush.radialGradient(listOf(LudoYellow, LudoYellowDark), Offset(centerRight, centerMidY), 2 * cellW))

                // Blue Triangle (Bottom)
                val bluePath = Path().apply {
                    moveTo(centerLeft, centerBottom)
                    lineTo(centerMidX, centerMidY)
                    lineTo(centerRight, centerBottom)
                    close()
                }
                drawPath(bluePath, Brush.radialGradient(listOf(LudoBlue, LudoBlueDark), Offset(centerMidX, centerBottom), 2 * cellW))

                // Center Gold Trophy Ring
                drawCircle(
                    brush = Brush.radialGradient(listOf(GoldPrimary, GoldDark)),
                    radius = cellW * 0.7f,
                    center = Offset(centerMidX, centerMidY)
                )
            }

            // 2. Overlay Safe Star Icons on safe spots
            val starPositions = listOf(
                BoardCoordinate(6, 1),
                BoardCoordinate(2, 6),
                BoardCoordinate(1, 8),
                BoardCoordinate(6, 12),
                BoardCoordinate(8, 13),
                BoardCoordinate(12, 8),
                BoardCoordinate(13, 6),
                BoardCoordinate(8, 2)
            )

            starPositions.forEach { starCoord ->
                Box(
                    modifier = Modifier
                        .offset(x = cellSize * starCoord.col, y = cellSize * starCoord.row)
                        .size(cellSize),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Safe Star",
                        tint = GoldPrimary.copy(alpha = 0.9f),
                        modifier = Modifier.size(cellSize * 0.65f)
                    )
                }
            }

            // 3. Center Crown Label
            Box(
                modifier = Modifier
                    .offset(x = cellSize * 6.5f, y = cellSize * 6.5f)
                    .size(cellSize * 2f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👑",
                    fontSize = (cellSize.value * 0.7f).sp
                )
            }

            // 4. Group all pawns by coordinate to handle multi-pawn stacking offsets cleanly
            val pawnPlacementMap = mutableMapOf<BoardCoordinate, MutableList<Pair<Player, Pawn>>>()
            players.forEach { player ->
                player.pawns.forEach { pawn ->
                    val coord = BoardPathUtils.getPawnCoordinate(pawn)
                    pawnPlacementMap.getOrPut(coord) { mutableListOf() }.add(Pair(player, pawn))
                }
            }

            // 5. Render Pawns with Interactive Touch, Stacking, and Glow
            pawnPlacementMap.forEach { (coord, pawnList) ->
                pawnList.forEachIndexed { index, (player, pawn) ->
                    val isCurrentPlayerPawn = player.isHuman && (currentPlayerColor == player.color)
                    val isMovable = isCurrentPlayerPawn && movablePawnIds.contains(pawn.id)

                    // Stacking offsets
                    val offsetX = when (pawnList.size) {
                        1 -> 0.dp
                        2 -> if (index == 0) (-3).dp else 3.dp
                        3 -> when (index) {
                            0 -> (-3).dp
                            1 -> 3.dp
                            else -> 0.dp
                        }
                        else -> when (index % 4) {
                            0 -> (-3).dp
                            1 -> 3.dp
                            2 -> (-3).dp
                            else -> 3.dp
                        }
                    }
                    val offsetY = when (pawnList.size) {
                        1 -> 0.dp
                        2 -> if (index == 0) (-3).dp else 3.dp
                        3 -> when (index) {
                            0 -> (-3).dp
                            1 -> (-3).dp
                            else -> 3.dp
                        }
                        else -> when (index % 4) {
                            0 -> (-3).dp
                            1 -> (-3).dp
                            2 -> 3.dp
                            else -> 3.dp
                        }
                    }

                    Box(
                        modifier = Modifier
                            .offset(
                                x = (cellSize * coord.col) + offsetX,
                                y = (cellSize * coord.row) + offsetY
                            )
                            .size(cellSize),
                        contentAlignment = Alignment.Center
                    ) {
                        PawnToken(
                            pawn = pawn,
                            playerColor = player.color,
                            isMovable = isMovable,
                            pulseScale = if (isMovable) pulseScale else 1.0f,
                            tokenSize = if (pawnList.size > 1) cellSize * 0.72f else cellSize * 0.85f,
                            onClick = {
                                if (isMovable) {
                                    onPawnSelected(pawn.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PawnToken(
    pawn: Pawn,
    playerColor: LudoColor,
    isMovable: Boolean,
    pulseScale: Float,
    tokenSize: Dp,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .size(tokenSize)
            .scale(pulseScale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = isMovable,
                onClick = onClick
            )
            .testTag("pawn_token_${playerColor.title}_${pawn.id}"),
        contentAlignment = Alignment.Center
    ) {
        // Glowing halo if movable
        if (isMovable) {
            Box(
                modifier = Modifier
                    .size(tokenSize * 1.3f)
                    .clip(CircleShape)
                    .background(GoldPrimary.copy(alpha = 0.45f))
            )
        }

        // Outer Shadow Ring
        Box(
            modifier = Modifier
                .size(tokenSize)
                .shadow(elevation = if (isMovable) 8.dp else 3.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            playerColor.primaryColor,
                            playerColor.darkColor,
                            Color(0xFF0F172A)
                        )
                    )
                )
                .border(
                    width = if (isMovable) 2.5.dp else 1.5.dp,
                    color = if (isMovable) GoldPrimary else Color.White.copy(alpha = 0.85f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // Inner Core Token with highlight dot
            Box(
                modifier = Modifier
                    .size(tokenSize * 0.5f)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.9f))
                    .border(1.dp, playerColor.darkColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${pawn.id + 1}",
                    color = playerColor.darkColor,
                    fontSize = (tokenSize.value * 0.28f).sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
