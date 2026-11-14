package com.yourssu.reststopmate.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourssu.reststopmate.data.Congestion
import com.yourssu.reststopmate.data.FeaturedMenu
import com.yourssu.reststopmate.data.RestStop
import com.yourssu.reststopmate.ui.theme.Background
import com.yourssu.reststopmate.ui.theme.Muted
import com.yourssu.reststopmate.ui.theme.Panel
import com.yourssu.reststopmate.ui.theme.RestStopColors

// ②③ 화면의 휴게소 카드. 기존 RestAreaRow.kt(My 화면용)와는 다른 카드다.
// 누르면 무엇을 할지는 카드가 정하지 않고 콜백(onClick, onAdd)으로 밖에 알린다.
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun RestAreaCard(
    restStop: RestStop,
    onClick: () -> Unit,
    onAdd: () -> Unit,     // + 버튼. 경유지 추가인지 즐겨찾기인지 팀에서 정하기 전까지는 빈 동작
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = Panel,
        shape = RoundedCornerShape(30.dp),
        shadowElevation = 8.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(restStop.name, fontSize = 26.sp, fontWeight = FontWeight.Bold)

            Text("혼잡도 : ${restStop.congestion.label}", fontSize = 18.sp, color = Muted)

            Text("식당 추천 메뉴", fontSize = 18.sp, fontWeight = FontWeight.Bold)

            val menu = restStop.featuredMenu
            if (menu != null){
                FeaturedMenuCard(menu = menu)
            }
            Row(verticalAlignment = Alignment.Bottom) {
                FlowRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    restStop.tags.forEach {AmenityChip(text = it, kind = ChipKind.Facility)}
                    restStop.brands.forEach {AmenityChip(text = it, kind = ChipKind.Brand)}
                }
                FilledIconButton(onClick = onAdd) {
                    Icon(Icons.Filled.Add, contentDescription = "추가")
                }
            }
        }
    }
}

private val previewRestStop = RestStop(
    id = "anseong-busan",
    name = "안성휴게소",
    congestion = Congestion.LOW,
    featuredMenu = FeaturedMenu("안성국밥", null, null),
    tags = listOf("주유소", "LPG 충전소", "전기차 충전소"),
    brands = listOf("할리스커피", "로띠번", "파스쿠찌", "JDX", "드롭탑"),
    latitude = 37.013407,
    longitude = 127.1448001,
)

// 대표 메뉴 있음 / 없음(null) 두 경우를 함께 확인한다.
@Preview(showBackground = true, widthDp = 900, heightDp = 640)
@Composable
private fun RestAreaCardPreview() {
    MaterialTheme(colorScheme = RestStopColors) {
        Surface(color = Background) {
            Row(Modifier.padding(24.dp), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                RestAreaCard(
                    restStop = previewRestStop,
                    onClick = { },
                    onAdd = { },
                    modifier = Modifier.width(400.dp),
                )
                RestAreaCard(
                    restStop = previewRestStop.copy(
                        id = "manghyang-busan",
                        name = "망향휴게소",
                        congestion = Congestion.MEDIUM,
                        featuredMenu = null,
                        brands = listOf("던킨도너츠"),
                    ),
                    onClick = { },
                    onAdd = { },
                    modifier = Modifier.width(400.dp),
                )
            }
        }
    }
}
