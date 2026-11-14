package com.yourssu.reststopmate.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.yourssu.reststopmate.ui.theme.Background
import com.yourssu.reststopmate.ui.theme.Muted
import java.text.NumberFormat
import java.util.Locale

@Composable
internal fun AllMenusPanel(state: SearchState, onBack: () -> Unit, onClose: () -> Unit, onRetry: () -> Unit) {
    Column(Modifier.fillMaxWidth().heightIn(max = 740.dp).padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(state.selectedStop?.name.orEmpty(), Modifier.weight(1f), fontSize = 25.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onClose) { Text("닫기") }
        }
        TextButton(onClick = onBack) { Text("대표 메뉴로") }
        Text(if (state.allMenusLoading || state.allMenusError) "전체 메뉴" else "전체 메뉴 ${state.allMenus.size}개",
            fontSize = 21.sp, fontWeight = FontWeight.Bold)
        when {
            state.allMenusLoading -> Box(Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            state.allMenusError -> {
                Text("전체 메뉴를 불러오지 못했어요. 연결을 확인해 주세요.", color = Muted)
                Button(onClick = onRetry) { Text("다시 시도") }
            }
            state.allMenus.isEmpty() -> Text("등록된 메뉴가 없어요.", color = Muted)
            else -> LazyVerticalGrid(
                columns = GridCells.Adaptive(180.dp),
                modifier = Modifier.fillMaxWidth().weight(1f, fill = false).testTag("all_menus_grid"),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 8.dp),
            ) {
                itemsIndexed(state.allMenus, key = { index, _ -> index }) { _, menu ->
                    Surface(color = Background, shape = RoundedCornerShape(16.dp)) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(shape = RoundedCornerShape(12.dp), color = Background) {
                                if (menu.imageUrl != null) {
                                    SubcomposeAsyncImage(model = menu.imageUrl, contentDescription = menu.name + " 사진",
                                        modifier = Modifier.fillMaxWidth().aspectRatio(4f / 3f), contentScale = ContentScale.Crop,
                                        loading = { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(Modifier.size(24.dp)) } },
                                        error = { MissingMenuPhoto() })
                                } else MissingMenuPhoto()
                            }
                            Text(menu.name, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                            Text(menu.priceWon?.let { NumberFormat.getNumberInstance(Locale.KOREA).format(it) + "원" }
                                ?: "가격 정보 없음", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            menu.description?.let { Text(it, fontSize = 15.sp, color = Muted) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MissingMenuPhoto() {
    Box(Modifier.fillMaxWidth().aspectRatio(4f / 3f), contentAlignment = Alignment.Center) {
        Text("사진을 불러올 수 없어요.", fontSize = 14.sp, color = Muted)
    }
}
