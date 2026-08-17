package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.model.LudoColor
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
fun DiceRoller(
    diceValue: Int,
    isRolling: Boolean,
    isHumanTurn: Boolean,
    playerColor: LudoColor?,
    canRoll: Boolean,
    consecutiveSixes: Int,
    onRollClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotationAngle = remember { Animatable(0f) }
    val bounceScale = remember { Animatable(1f) }

    LaunchedEffect(isRolling) {
        if (isRolling) {
            rotationAngle.animateTo(
                targetValue = rotationAngle.value + 720f,
                animationSpec = tween(durationMillis = 500, easing = LinearEasing)
            )
            bounceScale.animateTo(
                targetValue = 1.2f,
                animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
            )
            bounceScale.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "rollGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Surface(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .border(1.5.dp, if (canRoll) GoldPrimary.copy(alpha = glowAlpha) else DarkNavyElevated, RoundedCornerShape(16.dp))
            .testTag("dice_roller_container"),
        color = DarkNavySurface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Dice Visual
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .scale(bounceScale.value)
                    .rotate(rotationAngle.value)
                    .clickable(
                        enabled = canRoll,
                        onClick = onRollClick
                    )
                    .testTag("dice_face_button"),
                contentAlignment = Alignment.Center
            ) {
                // 3D Die Cube Face
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .shadow(12.dp, RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color.White,
                                    Color(0xFFF1F5F9),
                                    Color(0xFFCBD5E1)
                                )
                            )
                        )
                        .border(
                            2.dp,
                            Brush.linearGradient(listOf(GoldPrimary, GoldDark)),
                            RoundedCornerShape(14.dp)
                        )
                ) {
                    DicePips(
                        value = diceValue,
                        pipColor = if (diceValue == 6) LudoRed else Color(0xFF0F172A),
                        size = 60.dp
                    )
                }
            }

            // Status & Info
            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isHumanTurn) "YOUR TURN" else "${playerColor?.title?.uppercase() ?: "PLAYER"}'S TURN",
                        color = if (isHumanTurn) GoldPrimary else playerColor?.primaryColor ?: TextMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    if (consecutiveSixes > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(LudoRed)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "6s: $consecutiveSixes/3",
                                color = TextWhite,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = when {
                        isRolling -> "Rolling dice..."
                        canRoll -> "Tap button to roll"
                        isHumanTurn -> "Select pawn to move"
                        else -> "Waiting for move..."
                    },
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }

            // Roll Action Button
            Button(
                onClick = onRollClick,
                enabled = canRoll,
                modifier = Modifier
                    .height(44.dp)
                    .testTag("roll_dice_action_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    contentColor = Color(0xFF0F172A),
                    disabledContainerColor = DarkNavyElevated,
                    disabledContentColor = TextMuted
                )
            ) {
                Text(
                    text = if (isRolling) "🎲 ..." else "ROLL 🎲",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun DicePips(
    value: Int,
    pipColor: Color,
    size: Dp
) {
    val pipSize = size * 0.2f
    val pad = size * 0.16f

    Box(modifier = Modifier.fillMaxSize().padding(pad)) {
        when (value) {
            1 -> {
                Box(
                    modifier = Modifier
                        .size(pipSize * 1.3f)
                        .clip(CircleShape)
                        .background(pipColor)
                        .align(Alignment.Center)
                )
            }
            2 -> {
                Box(modifier = Modifier.size(pipSize).clip(CircleShape).background(pipColor).align(Alignment.TopStart))
                Box(modifier = Modifier.size(pipSize).clip(CircleShape).background(pipColor).align(Alignment.BottomEnd))
            }
            3 -> {
                Box(modifier = Modifier.size(pipSize).clip(CircleShape).background(pipColor).align(Alignment.TopStart))
                Box(modifier = Modifier.size(pipSize).clip(CircleShape).background(pipColor).align(Alignment.Center))
                Box(modifier = Modifier.size(pipSize).clip(CircleShape).background(pipColor).align(Alignment.BottomEnd))
            }
            4 -> {
                Box(modifier = Modifier.size(pipSize).clip(CircleShape).background(pipColor).align(Alignment.TopStart))
                Box(modifier = Modifier.size(pipSize).clip(CircleShape).background(pipColor).align(Alignment.TopEnd))
                Box(modifier = Modifier.size(pipSize).clip(CircleShape).background(pipColor).align(Alignment.BottomStart))
                Box(modifier = Modifier.size(pipSize).clip(CircleShape).background(pipColor).align(Alignment.BottomEnd))
            }
            5 -> {
                Box(modifier = Modifier.size(pipSize).clip(CircleShape).background(pipColor).align(Alignment.TopStart))
                Box(modifier = Modifier.size(pipSize).clip(CircleShape).background(pipColor).align(Alignment.TopEnd))
                Box(modifier = Modifier.size(pipSize).clip(CircleShape).background(pipColor).align(Alignment.Center))
                Box(modifier = Modifier.size(pipSize).clip(CircleShape).background(pipColor).align(Alignment.BottomStart))
                Box(modifier = Modifier.size(pipSize).clip(CircleShape).background(pipColor).align(Alignment.BottomEnd))
            }
            6 -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Box(modifier = Modifier.size(pipSize).clip(CircleShape).background(pipColor))
                        Box(modifier = Modifier.size(pipSize).clip(CircleShape).background(pipColor))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Box(modifier = Modifier.size(pipSize).clip(CircleShape).background(pipColor))
                        Box(modifier = Modifier.size(pipSize).clip(CircleShape).background(pipColor))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Box(modifier = Modifier.size(pipSize).clip(CircleShape).background(pipColor))
                        Box(modifier = Modifier.size(pipSize).clip(CircleShape).background(pipColor))
                    }
                }
            }
            else -> {
                Box(
                    modifier = Modifier
                        .size(pipSize * 1.3f)
                        .clip(CircleShape)
                        .background(pipColor)
                        .align(Alignment.Center)
                )
            }
        }
    }
}
