package com.yourssu.reststopmate.ui.my

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourssu.reststopmate.ui.theme.*
import com.yourssu.reststopmate.ui.components.*
import com.yourssu.reststopmate.data.mock.*


@Composable
internal fun MyScreen(frequent: LazyListState, meals: LazyListState, vertical: androidx.compose.foundation.ScrollState) {
    Column(Modifier.fillMaxSize().verticalScroll(vertical), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        PageHeading("My")
        Surface(Modifier.fillMaxWidth(), color = Panel, shape = RoundedCornerShape(32.dp), shadowElevation = 8.dp) {
            Column(Modifier.padding(30.dp), verticalArrangement = Arrangement.spacedBy(22.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("게스트", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Text("이 기기에서 이용 중", color = Muted, fontSize = 19.sp)
                }
                SectionHeading("자주 방문한 휴게소")
                RestAreaRow(mockRestAreas, frequent, "frequent_rest_areas")
                HorizontalDivider(color = Color(0xFFE2E3E6))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text("이번 달 휴게소 먹방 횟수", Modifier.weight(1f).semantics { heading() },
                        fontSize = 25.sp, fontWeight = FontWeight.Bold)
                    Text("8회", fontSize = 32.sp, fontWeight = FontWeight.Bold)
                }
                BoxWithConstraints(Modifier.fillMaxWidth()) {
                    val cardWidth = (maxWidth * 0.32f).coerceIn(240.dp, 420.dp)
                    LazyRow(state = meals, modifier = Modifier.fillMaxWidth().testTag("monthly_meals"),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(22.dp)) {
                        items(mockMeals, key = { it.id }) { record ->
                            Surface(Modifier.width(cardWidth), color = Panel,
                                shape = RoundedCornerShape(28.dp), shadowElevation = 7.dp) {
                                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Surface(shape = RoundedCornerShape(24.dp)) {
                                        Image(painterResource(record.image), contentDescription = record.menu,
                                            modifier = Modifier.fillMaxWidth().aspectRatio(390f / 230f),
                                            contentScale = ContentScale.Crop)
                                    }
                                    Text(record.restArea, fontSize = 20.sp, color = Muted)
                                    Text(record.menu, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
