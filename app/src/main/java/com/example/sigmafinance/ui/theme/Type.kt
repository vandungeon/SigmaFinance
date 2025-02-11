package com.example.sigmafinance.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.sigmafinance.R

val montserratFontFamily = FontFamily(
    Font(R.font.montserrat_black, FontWeight.Black),
    Font(R.font.montserrat_black_italic, FontWeight.Black, FontStyle.Italic),
    Font(R.font.montserrat_bold, FontWeight.Bold),
    Font(R.font.montserrat_bold_italic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.montserrat_extra_bold, FontWeight.ExtraBold),
    Font(R.font.montserrat_extra_bold_italic, FontWeight.ExtraBold, FontStyle.Italic),
    Font(R.font.montserrat_extra_light, FontWeight.ExtraLight),
    Font(R.font.montserrat_extra_light_italic, FontWeight.ExtraLight, FontStyle.Italic),
    Font(R.font.montserrat_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.montserrat_light, FontWeight.Light),
    Font(R.font.montserrat_light_italic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.montserrat_medium, FontWeight.Medium),
    Font(R.font.montserrat_medium_italic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_semi_bold, FontWeight.SemiBold),
    Font(R.font.montserrat_semi_bold_italic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.montserrat_thin, FontWeight.Thin),
    Font(R.font.montserrat_thin_italic, FontWeight.Thin, FontStyle.Italic)
)
val interFontFamily = FontFamily(
    Font(R.font.inter),
    Font(R.font.inter_italic),
)
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

val customTitle = TextStyle(
    color = periwinkle,
    fontFamily = montserratFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp,
    lineHeight = 28.sp,
    letterSpacing = 0.sp
)
val dialogHeader = TextStyle(
    fontFamily = montserratFontFamily,
    color =  periwinkle,
    fontWeight = FontWeight.W400,
    fontSize = 24.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.5.sp
)
val customText = TextStyle(
    fontFamily = interFontFamily,
    color = jordyBlue,
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.5.sp
)

val standardText =  TextStyle(
    fontFamily = montserratFontFamily,
    fontWeight = FontWeight.Normal,
    fontStyle = FontStyle.Normal,
    fontSize = 18.sp
)