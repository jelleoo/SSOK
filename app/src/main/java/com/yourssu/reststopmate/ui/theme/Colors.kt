package com.yourssu.reststopmate.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.lightColorScheme

internal val Background = Color(0xFFF7F8FA)
internal val Ink = Color(0xFF131417)
val RestStopColors = lightColorScheme(
    primary = Color(0xFF44464E), onPrimary = Color.White,
    background = Background, surface = Background, onSurface = Ink,
)

internal val Panel = Color.White
internal val Muted = Color(0xFF64666E)
internal val Selected = Color(0xFFF1F1F1)

// 휴게소 칩 색 (Home ②③). 시설은 진한 파랑, 브랜드는 연한 색. Figma 값과 다르면 여기만 고친다.
internal val ChipFacility = Color(0xFF2F5BD3)
internal val OnChipFacility = Color.White
internal val ChipBrand = Color(0xFFE8EEFC)
internal val OnChipBrand = Ink
