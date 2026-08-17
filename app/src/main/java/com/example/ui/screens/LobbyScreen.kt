package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entities.UserWalletEntity
import com.example.game.model.ContestTier
import com.example.game.model.GameMode
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

@Composable
fun LobbyScreen(
    wallet: UserWalletEntity?,
    contestTiers: List<ContestTier>,
    onSelectContest: (ContestTier) -> Unit,
    onOpenWallet: () -> Unit,
    onOpenSpinWheel: () -> Unit,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedModeFilter by remember { mutableStateOf<GameMode?>(null) }
    val filteredContests = remember(selectedModeFilter, contestTiers) {
        if (selectedModeFilter == null) contestTiers
        else contestTiers.filter { it.mode == selectedModeFilter }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .testTag("lobby_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Top Bar Header with Balance & Profile
        item {
            LobbyTopHeader(
                wallet = wallet,
                onAddCashClick = onOpenWallet,
                onProfileClick = onOpenProfile
            )
        }

        // 2. Hero Banner Card
        item {
            HeroLobbyBanner(
                onSpinClick = onOpenSpinWheel,
                onPlayQuick = {
                    val popular = contestTiers.find { it.isPopular } ?: contestTiers.first()
                    onSelectContest(popular)
                }
            )
        }

        // 3. Live Recent Winners Ticker
        item {
            LiveWinnersTicker()
        }

        // 4. Quick Action Rewards (Spin Wheel & Daily Bonus)
        item {
            RewardCardsRow(
                onSpinClick = onOpenSpinWheel
            )
        }

        // 5. Game Mode Filter Tabs
        item {
            GameModeFilterTabs(
                selectedMode = selectedModeFilter,
                onSelectMode = { selectedModeFilter = it }
            )
        }

        // 6. Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔥 ACTIVE CASH CONTESTS",
                    color = GoldPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${filteredContests.size} Tables Live",
                    color = CashGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 7. Contest List Items
        items(filteredContests, key = { it.id }) { contest ->
            ContestCard(
                contest = contest,
                onJoinClick = { onSelectContest(contest) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun LobbyTopHeader(
    wallet: UserWalletEntity?,
    onAddCashClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DarkNavySurface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // App Branding Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(LudoRed, GoldPrimary))
                        )
                        .border(1.5.dp, GoldPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🎲", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "CASH LUDO",
                        color = GoldPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "Real Cash Arena",
                        color = CashGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Wallet Balance Chip + Profile
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Wallet Chip
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(onClick = onAddCashClick)
                        .testTag("lobby_wallet_chip"),
                    color = DarkNavyElevated
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "₹${"%.2f".format(wallet?.totalBalance ?: 0.0)}",
                            color = GoldPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(CashGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Cash",
                                tint = Color(0xFF0F172A),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Profile Avatar
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DarkNavyElevated)
                        .border(1.dp, GoldPrimary, CircleShape)
                        .clickable(onClick = onProfileClick)
                        .testTag("lobby_profile_avatar"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "👤", fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun HeroLobbyBanner(
    onSpinClick: () -> Unit,
    onPlayQuick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(12.dp, RoundedCornerShape(20.dp))
            .testTag("hero_lobby_banner"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkNavyCard)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
            // Background Image
            Image(
                painter = painterResource(id = R.drawable.banner_cash_ludo),
                contentDescription = "Cash Ludo Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF0B111E).copy(alpha = 0.92f),
                                Color(0xFF0B111E).copy(alpha = 0.70f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(GoldPrimary)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "⚡ INSTANT WITHDRAWAL",
                        color = Color(0xFF0F172A),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "PLAY LUDO & WIN CASH",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "100% Fair Dice • Instant Payouts",
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onPlayQuick,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color(0xFF0F172A)),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(text = "PLAY QUICK DUEL 🎲", fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                    Button(
                        onClick = onSpinClick,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkNavyElevated, contentColor = TextWhite),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(text = "DAILY SPIN 🎡", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun LiveWinnersTicker() {
    val winners = listOf(
        "🎉 Rohit_King won ₹90 in 1v1 Duel",
        "🔥 Alex99 won ₹180 in High Roller",
        "🏆 Priya_Cash won ₹350 in Mega Pot",
        "⚡ DevStriker won ₹45 in Speed Rush"
    )
    val randomWinner = remember { winners.random() }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, CashGreen.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
        color = DarkNavySurface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(CashGreen)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "LIVE: $randomWinner",
                color = TextGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun RewardCardsRow(
    onSpinClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Spin Card
        Surface(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .border(1.5.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .clickable(onClick = onSpinClick)
                .testTag("lobby_spin_card"),
            color = DarkNavyCard
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🎡", fontSize = 28.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = "Lucky Spin", color = GoldPrimary, fontSize = 13.sp, fontWeight = FontWeight.Black)
                    Text(text = "Win up to ₹500", color = TextMuted, fontSize = 10.sp)
                }
            }
        }

        // Daily Bonus Card
        Surface(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .border(1.5.dp, CashGreen.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .clickable(onClick = onSpinClick)
                .testTag("lobby_bonus_card"),
            color = DarkNavyCard
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🎁", fontSize = 28.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = "Daily Reward", color = CashGreen, fontSize = 13.sp, fontWeight = FontWeight.Black)
                    Text(text = "Claim Cash Gift", color = TextMuted, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun GameModeFilterTabs(
    selectedMode: GameMode?,
    onSelectMode: (GameMode?) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            val isSelected = selectedMode == null
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, if (isSelected) GoldPrimary else DarkNavyElevated, RoundedCornerShape(20.dp))
                    .clickable { onSelectMode(null) }
                    .testTag("filter_all"),
                color = if (isSelected) GoldPrimary else DarkNavyCard
            ) {
                Text(
                    text = "🌟 All Tables",
                    color = if (isSelected) Color(0xFF0F172A) else TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }

        items(GameMode.values()) { mode ->
            val isSelected = selectedMode == mode
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, if (isSelected) GoldPrimary else DarkNavyElevated, RoundedCornerShape(20.dp))
                    .clickable { onSelectMode(mode) }
                    .testTag("filter_${mode.name}"),
                color = if (isSelected) GoldPrimary else DarkNavyCard
            ) {
                Text(
                    text = mode.title,
                    color = if (isSelected) Color(0xFF0F172A) else TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun ContestCard(
    contest: ContestTier,
    onJoinClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .border(
                1.5.dp,
                if (contest.isPopular) GoldPrimary else DarkNavyElevated,
                RoundedCornerShape(16.dp)
            )
            .testTag("contest_card_${contest.id}"),
        color = DarkNavyCard
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            // Header Row: Contest Name + Popular Tag + Player Count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = contest.name,
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (contest.isPopular) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(GoldPrimary)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "POPULAR",
                                color = Color(0xFF0F172A),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = "Players",
                        tint = TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${contest.playerCount} Players",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Body: Prize Pool & Entry Fee details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Prize Pool Section
                Column {
                    Text(text = "PRIZE POOL", color = TextMuted, fontSize = 10.sp, letterSpacing = 1.sp)
                    Text(
                        text = if (contest.prizePool > 0) "₹${"%.2f".format(contest.prizePool)}" else "FREE",
                        color = GoldPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "1st: ₹${"%.2f".format(contest.firstPrize)}",
                        color = CashGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Play Button
                Button(
                    onClick = onJoinClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (contest.entryFee == 0.0) DarkNavyElevated else GoldPrimary,
                        contentColor = if (contest.entryFee == 0.0) TextWhite else Color(0xFF0F172A)
                    ),
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("play_contest_${contest.id}")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (contest.entryFee > 0) "PLAY ₹${contest.entryFee.toInt()}" else "PLAY FREE",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
