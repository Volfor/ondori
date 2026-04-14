package com.volfor.ondori.app.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.volfor.ondori.R

val manrope = GoogleFont("Manrope")
val jakarta = GoogleFont("Plus Jakarta Sans")

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

val displayFontFamily = FontFamily(
    Font(googleFont = manrope, fontProvider = provider, weight = FontWeight.ExtraBold),
)

val titleFontFamily = FontFamily(
    Font(googleFont = jakarta, fontProvider = provider, weight = FontWeight.Bold),
)

val bodyFontFamily = FontFamily(
    Font(googleFont = jakarta, fontProvider = provider),
)

private val TitleSmallStyle = TextStyle(
    fontFamily = titleFontFamily,
    fontWeight = FontWeight.Bold,
    letterSpacing = 1.1.sp,
    fontSize = 12.sp,
)

private val TitleMediumStyle = TextStyle(
    fontSize = 16.sp,
    fontWeight = FontWeight.Bold,
    fontFamily = titleFontFamily,
)

private val TitleLargeStyle = TextStyle(
    fontFamily = titleFontFamily,
    fontWeight = FontWeight.Bold,
    letterSpacing = 1.6.sp,
    fontSize = 18.sp,
)

private val DisplayMediumStyle = TextStyle(
    fontSize = 48.sp,
    fontWeight = FontWeight.ExtraBold,
    fontFamily = displayFontFamily,
)


private val DisplayLargeStyle = TextStyle(
    fontFamily = displayFontFamily,
    fontSize = 57.sp,
    fontWeight = FontWeight.ExtraBold,
)

private val LabelMediumStyle = TextStyle(
    fontFamily = bodyFontFamily,
    fontSize = 16.sp,
)

private val BodyLargeStyle = TextStyle(
    fontFamily = bodyFontFamily,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.sp,
    fontWeight = FontWeight.SemiBold,
)

val Typography = Typography(
    displayMedium = DisplayMediumStyle,
    displayLarge = DisplayLargeStyle,
    titleSmall = TitleSmallStyle,
    titleMedium = TitleMediumStyle,
    titleLarge = TitleLargeStyle,
    bodyLarge = BodyLargeStyle,
    labelMedium = LabelMediumStyle,
)