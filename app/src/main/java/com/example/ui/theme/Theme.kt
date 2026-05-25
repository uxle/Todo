package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val CyberColorScheme =
  darkColorScheme(
    primary = CyberCyan,
    secondary = CyberPink,
    tertiary = CyberSuccess,
    background = CyberDark,
    surface = CyberSurface,
    surfaceVariant = CyberSurfaceVariant,
    onPrimary = CyberDark,
    onBackground = CyberTextPrimary,
    onSurface = CyberTextPrimary,
    onSurfaceVariant = CyberTextSecondary,
    error = CyberDanger
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark
  dynamicColor: Boolean = false, // Force custom theme
  content: @Composable () -> Unit,
) {
  val colorScheme = CyberColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
