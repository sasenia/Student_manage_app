package com.example.studentmanageapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.studentmanageapp.R

// 🎨 연두색 테마
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF9CCC65),       // 연한 올리브 연두 (Lime 400)
    secondary = Color(0xFFDCEDC8),     // 연두 배경 (Lime 100)
    tertiary = Color(0xFFC5E1A5),      // 중간 연두 포인트 (Lime 200)
    background = Color(0xFFF1F8E9),    // 아주 연한 배경 (Lime 50)
    surface = Color.White,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
)

// 🌙 다크 테마 (기존 유지)
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

// 🖋️ 한컴 말랑말랑 Regular 폰트 설정
private val HancomMalangFont = FontFamily(
    Font(R.font.hancom_malangmalang_regular, FontWeight.Normal)
)

// ✏️ 전체 Typography 설정
private val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = HancomMalangFont,
        fontSize = 32.sp
    ),
    titleLarge = TextStyle(
        fontFamily = HancomMalangFont,
        fontSize = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = HancomMalangFont,
        fontSize = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = HancomMalangFont,
        fontSize = 14.sp
    )
)

@Composable
fun StudentManageAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}