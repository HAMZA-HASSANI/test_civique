package com.example.testcivique.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val CivicBlue = Color(0xFF2457D6)
val CivicNavy = Color(0xFF11264D)
val CivicSky = Color(0xFF6EA8FF)
val CivicRed = Color(0xFFE6575E)
val CivicGreen = Color(0xFF21A179)
val CivicGold = Color(0xFFF2B84B)
val CivicCream = Color(0xFFF7F8FC)

private val LightColors = lightColorScheme(
    primary = CivicBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCE6FF),
    onPrimaryContainer = CivicNavy,
    secondary = CivicRed,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDADC),
    tertiary = CivicGreen,
    tertiaryContainer = Color(0xFFC8F1E3),
    background = CivicCream,
    surface = Color.White,
    surfaceVariant = Color(0xFFEEF1F7),
    onSurfaceVariant = Color(0xFF5D6472),
    outline = Color(0xFFD4D8E2),
    error = Color(0xFFBA1A1A),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFAFC6FF),
    onPrimary = Color(0xFF002E6E),
    primaryContainer = Color(0xFF16449B),
    secondary = Color(0xFFFFB2B5),
    tertiary = Color(0xFF8AD7BB),
    background = Color(0xFF0E1420),
    surface = Color(0xFF161D2A),
    surfaceVariant = Color(0xFF222B3A),
    onSurfaceVariant = Color(0xFFC3C8D2),
    outline = Color(0xFF3A4557),
)

private val CivicTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 34.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.5).sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 19.sp,
        lineHeight = 25.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
    ),
)

@Composable
fun CivicTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = CivicTypography,
        content = content,
    )
}
