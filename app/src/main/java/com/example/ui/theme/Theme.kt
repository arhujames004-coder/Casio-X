package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val CasioDarkColorScheme = darkColorScheme(
  primary = CasioKeyEquals,
  onPrimary = Color.White,
  primaryContainer = CasioKeyOperation,
  onPrimaryContainer = CasioKeyTextPrimary,
  secondary = CasioShiftGold,
  onSecondary = Color.Black,
  secondaryContainer = CasioChassisHighlight,
  onSecondaryContainer = CasioKeyTextPrimary,
  tertiary = CasioAlphaRuby,
  onTertiary = Color.White,
  background = CasioChassisDark,
  onBackground = CasioKeyTextPrimary,
  surface = CasioChassisSurface,
  onSurface = CasioKeyTextPrimary,
  surfaceVariant = CasioKeyOperation,
  onSurfaceVariant = CasioKeyTextSecondary,
  outline = CasioChassisBorder
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to Dark mode for the scientific calculator experience
  dynamicColor: Boolean = false, // Preserve Casio hardware styling
  content: @Composable () -> Unit,
) {
  val colorScheme = CasioDarkColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
