package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.local.repository.CashLudoRepository
import com.example.game.model.ContestTier
import com.example.ui.components.SpinWheelDialog
import com.example.ui.screens.GameScreen
import com.example.ui.screens.LobbyScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.TournamentScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.CashGreen
import com.example.ui.theme.CashLudoTheme
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavyElevated
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.GamePhase
import com.example.ui.viewmodel.LudoGameViewModel
import com.example.ui.viewmodel.WalletViewModel
import kotlinx.coroutines.launch

enum class AppScreen(val title: String, val icon: ImageVector, val tag: String) {
    LOBBY("Lobby", Icons.Default.Casino, "nav_lobby"),
    WALLET("Wallet", Icons.Default.AccountBalanceWallet, "nav_wallet"),
    TOURNAMENTS("Leagues", Icons.Default.EmojiEvents, "nav_leagues"),
    PROFILE("Profile", Icons.Default.Person, "nav_profile"),
    GAME("Game", Icons.Default.Casino, "nav_game")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CashLudoTheme {
                CashLudoApp()
            }
        }
    }
}

@Composable
fun CashLudoApp() {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember {
        CashLudoRepository(
            walletDao = database.walletDao(),
            transactionDao = database.transactionDao(),
            matchHistoryDao = database.matchHistoryDao()
        )
    }

    val gameViewModel: LudoGameViewModel = viewModel { LudoGameViewModel(repository) }
    val walletViewModel: WalletViewModel = viewModel { WalletViewModel(repository) }

    val walletState by walletViewModel.wallet.collectAsStateWithLifecycle()
    val transactions by walletViewModel.transactions.collectAsStateWithLifecycle()
    val walletUiState by walletViewModel.uiState.collectAsStateWithLifecycle()

    val gameState by gameViewModel.gameState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var currentScreen by remember { mutableStateOf(AppScreen.LOBBY) }

    // Listen for wallet status messages
    LaunchedEffect(walletUiState.statusMessage) {
        walletUiState.statusMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            walletViewModel.clearStatusMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (currentScreen != AppScreen.GAME) {
                NavigationBar(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("bottom_navigation_bar"),
                    containerColor = DarkNavySurface,
                    contentColor = TextWhite
                ) {
                    val navItems = listOf(
                        AppScreen.LOBBY,
                        AppScreen.WALLET,
                        AppScreen.TOURNAMENTS,
                        AppScreen.PROFILE
                    )

                    navItems.forEach { screen ->
                        val isSelected = currentScreen == screen
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentScreen = screen },
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.title,
                                    tint = if (isSelected) GoldPrimary else TextMuted
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    color = if (isSelected) GoldPrimary else TextMuted,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = DarkNavyElevated
                            ),
                            modifier = Modifier.testTag(screen.tag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (currentScreen) {
                AppScreen.LOBBY -> {
                    LobbyScreen(
                        wallet = walletState,
                        contestTiers = gameViewModel.contestTiers,
                        onSelectContest = { tier ->
                            gameViewModel.startContest(
                                tier = tier,
                                onSuccess = { currentScreen = AppScreen.GAME },
                                onError = { errorMsg ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar(errorMsg)
                                    }
                                }
                            )
                        },
                        onOpenWallet = { currentScreen = AppScreen.WALLET },
                        onOpenSpinWheel = { walletViewModel.showSpinWheel(true) },
                        onOpenProfile = { currentScreen = AppScreen.PROFILE }
                    )
                }
                AppScreen.WALLET -> {
                    WalletScreen(
                        wallet = walletState,
                        transactions = transactions,
                        selectedFilter = walletUiState.selectedTxnFilter,
                        onFilterSelect = { walletViewModel.setTxnFilter(it) },
                        showAddCashDialog = walletUiState.isAddCashDialogVisible,
                        showWithdrawDialog = walletUiState.isWithdrawDialogVisible,
                        onShowAddCash = { walletViewModel.showAddCash(it) },
                        onShowWithdraw = { walletViewModel.showWithdraw(it) },
                        onAddCashSubmit = { amount, method ->
                            walletViewModel.addCash(amount, method) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Added ₹${"%.2f".format(amount)} via $method!")
                                }
                            }
                        },
                        onWithdrawSubmit = { amount, account ->
                            walletViewModel.withdraw(amount, account) { success, msg ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(msg)
                                }
                            }
                        }
                    )
                }
                AppScreen.TOURNAMENTS -> {
                    TournamentScreen(
                        onJoinTournament = {
                            val grandTier = gameViewModel.contestTiers.find { it.id == "tier_4p_50" }
                                ?: gameViewModel.contestTiers.first()
                            gameViewModel.startContest(
                                tier = grandTier,
                                onSuccess = { currentScreen = AppScreen.GAME },
                                onError = { errorMsg ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar(errorMsg)
                                    }
                                }
                            )
                        }
                    )
                }
                AppScreen.PROFILE -> {
                    ProfileScreen(
                        wallet = walletState,
                        onUpdateProfile = { name, avatar, skin ->
                            walletViewModel.updateProfile(name, avatar, skin)
                        }
                    )
                }
                AppScreen.GAME -> {
                    GameScreen(
                        gameState = gameState,
                        onRollDice = { gameViewModel.rollDice() },
                        onSelectPawn = { pawnId -> gameViewModel.selectPawn(pawnId) },
                        onSendChatMessage = { msg, isEmoji -> gameViewModel.sendChatMessage(msg, isEmoji) },
                        onForfeitGame = {
                            gameViewModel.forfeitGame()
                            currentScreen = AppScreen.LOBBY
                        },
                        onPlayAgain = {
                            gameState.currentContest?.let { tier ->
                                gameViewModel.startContest(
                                    tier = tier,
                                    onSuccess = { currentScreen = AppScreen.GAME },
                                    onError = { errorMsg ->
                                        scope.launch {
                                            snackbarHostState.showSnackbar(errorMsg)
                                        }
                                    }
                                )
                            }
                        },
                        onBackToLobby = {
                            gameViewModel.resetGameToIdle()
                            currentScreen = AppScreen.LOBBY
                        }
                    )
                }
            }

            // Lucky Spin Wheel Dialog Global Overlay
            if (walletUiState.isSpinWheelDialogVisible) {
                SpinWheelDialog(
                    onDismiss = { walletViewModel.showSpinWheel(false) },
                    onClaimReward = { reward ->
                        walletViewModel.claimSpinBonus(reward)
                    }
                )
            }
        }
    }
}

