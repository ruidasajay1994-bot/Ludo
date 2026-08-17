package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CashLudoColorScheme = darkColorScheme(
  primary = GoldPrimary,
  onPrimary = DarkNavyBg,
  primaryContainer = DarkNavyElevated,
  onPrimaryContainer = GoldPrimary,
  secondary = CashGreen,
  onSecondary = DarkNavyBg,
  secondaryContainer = DarkNavyCard,
  onSecondaryContainer = CashGreen,
  tertiary = AccentPurple,
  onTertiary = TextWhite,
  background = DarkNavyBg,
  onBackground = TextWhite,
  surface = DarkNavySurface,
  onSurface = TextWhite,
  surfaceVariant = DarkNavyCard,
  onSurfaceVariant = TextMuted,
  outline = DarkNavyBorder,
  error = LudoRed,
  onError = TextWhite
)

@Composable
fun CashLudoTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = CashLudoColorScheme,
    typography = Typography,
    content = content
  )
}

// Backward compatibility alias
@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  CashLudoTheme(content = content)
}

