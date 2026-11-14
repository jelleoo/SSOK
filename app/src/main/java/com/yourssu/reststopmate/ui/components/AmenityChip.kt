package com.yourssu.reststopmate.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourssu.reststopmate.ui.theme.ChipBrand
import com.yourssu.reststopmate.ui.theme.ChipFacility
import com.yourssu.reststopmate.ui.theme.OnChipBrand
import com.yourssu.reststopmate.ui.theme.OnChipFacility
import com.yourssu.reststopmate.ui.theme.RestStopColors

// 칩 종류: 편의시설(tags)인지 입점 브랜드(brands)인지
internal enum class ChipKind { Facility, Brand }

@Composable
internal fun AmenityChip(
    text: String,
    kind: ChipKind,
    modifier: Modifier = Modifier,
) {
    val background = when(kind){
        ChipKind.Facility -> ChipFacility
        ChipKind.Brand -> ChipBrand
    }
    val content = when(kind){
        ChipKind.Facility -> OnChipFacility
        ChipKind.Brand -> OnChipBrand
    }
    Surface(
        modifier = modifier,
        color = background,
        contentColor = content,
        shape = CircleShape
    ) {
        Text(text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}

// FlowRow: 한 줄에 다 안 들어가면 다음 줄로 넘긴다. RestAreaCard에서도 이렇게 쓴다.
@OptIn(ExperimentalLayoutApi::class)
@Preview(showBackground = true, widthDp = 420)
@Composable
private fun AmenityChipPreview() {
    MaterialTheme(colorScheme = RestStopColors) {
        FlowRow(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf("주유소", "LPG 충전소", "전기차 충전소").forEach { AmenityChip(it, ChipKind.Facility) }
            listOf("할리스커피", "로띠번", "파스쿠찌", "JDX", "드롭탑").forEach { AmenityChip(it, ChipKind.Brand) }
        }
    }
}
