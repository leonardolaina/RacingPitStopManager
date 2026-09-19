package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MotorsportDarkColorScheme = darkColorScheme(
  primary = ElectricCyan,
  onPrimary = CarbonBlack,
  primaryContainer = CarbonSurfaceElevated,
  onPrimaryContainer = ElectricCyan,
  secondary = RacingRed,
  onSecondary = Color.White,
  secondaryContainer = RacingRedDark,
  onSecondaryContainer = Color.White,
  tertiary = TimingPurple,
  onTertiary = CarbonBlack,
  background = CarbonBlack,
  onBackground = TextPrimary,
  surface = CarbonSurface,
  onSurface = TextPrimary,
  surfaceVariant = CarbonSurfaceElevated,
  onSurfaceVariant = TextSecondary,
  outline = CarbonBorder,
  error = RacingRed,
  onError = Color.White
)

@Composable
fun MotorsportManagerTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = MotorsportDarkColorScheme,
    typography = Typography,
    content = content
  )
}
