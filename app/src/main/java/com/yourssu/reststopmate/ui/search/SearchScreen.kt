package com.yourssu.reststopmate.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourssu.reststopmate.data.search.RestStop
import com.yourssu.reststopmate.ui.components.SectionHeading
import com.yourssu.reststopmate.ui.theme.Muted
import com.yourssu.reststopmate.ui.theme.Panel

@Composable
internal fun SearchScreen(
    state: SearchState,
    onQuery: (String) -> Unit,
    onSelect: (RestStop, Boolean) -> Unit,
    onRetry: () -> Unit,
    onHistoryRetry: () -> Unit,
    onDismissMessage: () -> Unit,
    onCloseMenu: () -> Unit,
    onRetryMenu: () -> Unit,
    onAllMenus: () -> Unit,
    onBackToFeatured: () -> Unit,
    resultsScroll: LazyListState,
    historyScroll: LazyListState,
) {
    val recentMode = state.query.isBlank()
    val rows = if (recentMode) state.recent else state.results
    val keyboard = LocalSoftwareKeyboardController.current
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(state.message) {
        state.message?.let { snackbar.showSnackbar(it); onDismissMessage() }
    }
    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            OutlinedTextField(value = state.query, onValueChange = onQuery,
                modifier = Modifier.fillMaxWidth().testTag("rest_area_search"),
                placeholder = { Text("휴게소 이름을 검색하세요") },
                trailingIcon = {
                    if (state.query.isNotEmpty()) TextButton(onClick = { onQuery(""); keyboard?.hide() }) { Text("지우기") }
                },
                singleLine = true, shape = RoundedCornerShape(24.dp),
                textStyle = LocalTextStyle.current.copy(fontSize = 22.sp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Panel, focusedContainerColor = Panel))
            SectionHeading(if (recentMode) "최근 검색 항목" else "검색 결과")
            val loading = if (recentMode) state.historyLoading else state.loading
            val failed = if (recentMode) state.historyError else state.networkError
            when {
                loading -> CircularProgressIndicator(Modifier.testTag("search_loading"))
                failed -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(if (recentMode) "최근 검색 기록을 불러오지 못했어요." else "휴게소 정보를 불러오지 못했어요. 연결을 확인해 주세요.")
                    Button(onClick = if (recentMode) onHistoryRetry else onRetry) { Text("다시 시도") }
                }
                rows.isEmpty() -> Text(if (recentMode) "최근 검색한 휴게소가 없어요." else "검색 결과가 없어요.", fontSize = 20.sp, color = Muted)
                else -> BoxWithConstraints(Modifier.fillMaxWidth()) {
                    val cardWidth = (maxWidth * 0.38f).coerceIn(220.dp, 440.dp)
                    LazyRow(state = if (recentMode) historyScroll else resultsScroll,
                        modifier = Modifier.fillMaxWidth().testTag(if (recentMode) "search_history" else "search_results"),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        items(rows, key = { it.id }) { stop ->
                            Surface(onClick = {
                                keyboard?.hide()
                                onSelect(stop, !recentMode)
                            }, enabled = !state.saving, modifier = Modifier.width(cardWidth).testTag("rest_stop_${stop.id}"),
                                color = Panel, shape = RoundedCornerShape(26.dp), shadowElevation = 7.dp) {
                                Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(stop.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                    Text(stop.description, fontSize = 18.sp, color = Muted)
                                }
                            }
                        }
                    }
                }
            }
        }
        SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter))
        if (state.selectedStop != null) FeaturedMenuDialog(state, onCloseMenu, onRetryMenu, onAllMenus, onBackToFeatured)
    }
}
