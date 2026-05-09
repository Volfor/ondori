package com.volfor.ondori.app.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.volfor.ondori.R

val manrope = GoogleFont("Manrope")
val jakarta = GoogleFont("Plus Jakarta Sans")

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val bodyFontFamily = FontFamily(
    Font(googleFont = jakarta, fontProvider = provider, weight = FontWeight.ExtraLight),
    Font(googleFont = jakarta, fontProvider = provider, weight = FontWeight.Light),
    Font(googleFont = jakarta, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = jakarta, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = jakarta, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = jakarta, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = jakarta, fontProvider = provider, weight = FontWeight.ExtraBold)
)

val displayFontFamily = FontFamily(
    Font(googleFont = manrope, fontProvider = provider, weight = FontWeight.ExtraLight),
    Font(googleFont = manrope, fontProvider = provider, weight = FontWeight.Light),
    Font(googleFont = manrope, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = manrope, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = manrope, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = manrope, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = manrope, fontProvider = provider, weight = FontWeight.ExtraBold)
)

val baseline = Typography()

val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = displayFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = displayFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = displayFontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = displayFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = displayFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = displayFontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = displayFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = displayFontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = displayFontFamily),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = bodyFontFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = bodyFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = bodyFontFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = bodyFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = bodyFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = bodyFontFamily),
)