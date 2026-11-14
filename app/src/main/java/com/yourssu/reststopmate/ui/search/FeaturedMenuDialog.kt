package com.yourssu.reststopmate.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.SubcomposeAsyncImage
import com.yourssu.reststopmate.ui.theme.Muted
import com.yourssu.reststopmate.ui.theme.Panel
import java.text.NumberFormat
import java.util.Locale

@Composable
internal fun FeaturedMenuDialog(state: SearchState, onClose: () -> Unit, onRetry: () -> Unit, onAllMenus: () -> Unit, onBackToFeatured: () -> Unit) {
    val stop = state.selectedStop ?: return
    Dialog(onDismissRequest = { if (state.showAllMenus) onBackToFeatured() else onClose() }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(Modifier.padding(16.dp).widthIn(max = if (state.showAllMenus) 900.dp else 600.dp).fillMaxWidth().heightIn(max = 760.dp),
            shape = RoundedCornerShape(28.dp), color = Panel) {
            if (state.showAllMenus) {
                AllMenusPanel(state, onBackToFeatured, onClose, onAllMenus)
            } else Column(Modifier.verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(stop.name, Modifier.weight(1f), fontSize = 25.sp, fontWeight = FontWeight.Bold)
                    TextButton(onClick = onClose) { Text("닫기") }
                }
                Text(stop.description, color = Muted, fontSize = 16.sp)
                Text("대표 메뉴", fontSize = 19.sp, fontWeight = FontWeight.Bold)
                when {
                    state.menuLoading -> Box(Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                    state.menuError -> {
                        Text("대표 메뉴를 불러오지 못했어요. 연결을 확인해 주세요.")
                        Button(onClick = onRetry) { Text("다시 시도") }
                    }
                    state.featuredMenu == null -> Text("등록된 대표 메뉴가 없어요.", color = Muted)
                    else -> {
                        val menu = state.featuredMenu
                        if (menu.imageUrl != null) {
                            Surface(shape = RoundedCornerShape(18.dp)) {
                                SubcomposeAsyncImage(model = menu.imageUrl, contentDescription = menu.name + " 사진",
                                    modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f), contentScale = ContentScale.Crop,
                                    loading = { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() } },
                                    error = { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("사진을 불러올 수 없어요.", color = Muted) } })
                            }
                        }
                        Text(menu.name, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                        Text(menu.priceWon?.let { NumberFormat.getNumberInstance(Locale.KOREA).format(it) + "원" }
                            ?: if (menu.priceLoadFailed) "가격을 불러오지 못했어요." else "가격 정보 없음",
                            fontSize = 23.sp, fontWeight = FontWeight.Bold)
                        menu.description?.let { Text(it, color = Muted, fontSize = 17.sp) }
                        if (menu.priceLoadFailed) OutlinedButton(onClick = onRetry) { Text("가격 다시 불러오기") }
                    }
                }
                Button(onClick = onAllMenus, modifier = Modifier.fillMaxWidth()) { Text("전체 메뉴 보기") }
            }
        }
    }
}
