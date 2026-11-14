package com.yourssu.reststopmate.ui.nearby

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourssu.reststopmate.data.Congestion
import com.yourssu.reststopmate.data.FeaturedMenu
import com.yourssu.reststopmate.data.RestStop
import com.yourssu.reststopmate.ui.UiState
import com.yourssu.reststopmate.ui.components.PageHeading
import com.yourssu.reststopmate.ui.components.RestAreaCard
import com.yourssu.reststopmate.ui.theme.Background
import com.yourssu.reststopmate.ui.theme.Muted
import com.yourssu.reststopmate.ui.theme.RestStopColors
import kotlinx.coroutines.launch

// 바깥: ViewModel을 만들고 상태를 구독한다. 나중에 HomeScreen(넓은 화면)에서 이 함수를 부른다.
@Composable
internal fun NearbyRoute(
    onRestStopClick: (restStop: RestStop) -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    val viewModel: NearbyViewModel = viewModel(factory = NearbyViewModel.factory())
    val state by viewModel.state.collectAsStateWithLifecycle()
    NearbyContent(state, onRestStopClick, onRetry = viewModel::load, listState = listState)
}

// 안쪽: 상태에 따라 그린다. (MenuListContent와 같은 구조)
@Composable
internal fun NearbyContent(
    state: UiState<List<RestStop>>,
    onRestStopClick: (restStop: RestStop) -> Unit,
    onRetry: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    when (state) {
        is UiState.Success -> NearbyScreen(state.data, onRestStopClick, listState)
        UiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        UiState.Empty -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("지원 구간 밖이거나 남은 휴게소가 없어요", color = Muted, fontSize = 20.sp)
        }
        is UiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(state.message, fontSize = 20.sp)
                Button(onClick = onRetry) { Text("다시 시도") }
            }
        }
    }
}

// ② 가까운 휴게소 (가로 화면). 카드 가로 목록 + 오른쪽 아래 이전/다음 버튼
// listState를 밖에서 받는 이유: 탭을 바꿨다 와도 스크롤 위치를 지키려면
// 나중에 MainTabsScreen이 이 상태를 들고 있어야 한다 (AGENTS.md: 목록별 스크롤 위치 보존)
@Composable
internal fun NearbyScreen(
    restStops: List<RestStop>,
    onRestStopClick: (restStop: RestStop) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    // 버튼을 누를 때 스크롤 애니메이션(suspend 함수)을 실행할 곳
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        PageHeading("가까운 휴게소")

        BoxWithConstraints(Modifier.weight(1f).fillMaxWidth()) {
            // 화면에 카드 2장이 보이도록 폭을 계산한다 (Figma ②)
            val cardWidth = (maxWidth - 24.dp) / 2

            LazyRow(
                state = listState,
                contentPadding = PaddingValues(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                items(restStops, key = { it.id }) { restStop -> RestAreaCard(
                    restStop = restStop,
                    onClick = { onRestStopClick(restStop) },
                    onAdd = { },
                    modifier = Modifier.width(cardWidth),
                ) }
            }
        }

        Row(
            modifier = Modifier.align(Alignment.End),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // 이전 카드로: 지금 맨 앞 카드 번호 - 1 (0보다 작아지지 않게)
            FilledTonalIconButton(onClick = {
                scope.launch {
                    val target = (listState.firstVisibleItemIndex - 1).coerceAtLeast(0)
                    listState.animateScrollToItem(target)
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "이전 휴게소")
            }

            FilledTonalIconButton(onClick = {
                scope.launch {
                    val target = (listState.firstVisibleItemIndex + 1).coerceAtMost(restStops.lastIndex)
                    listState.animateScrollToItem(target)
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "다음 휴게소")
            }
        }
    }
}

private val previewRestStops = listOf(
    RestStop(
        "anseong-busan", "안성휴게소", Congestion.LOW, FeaturedMenu("안성국밥", null, null),
        listOf("주유소", "LPG 충전소", "전기차 충전소"), listOf("할리스커피", "로띠번", "파스쿠찌"),
        37.013407, 127.1448001,
    ),
    RestStop(
        "manghyang-busan", "망향휴게소", Congestion.MEDIUM, FeaturedMenu("한우국밥", null, null),
        listOf("주유소", "전기차 충전소"), listOf("던킨도너츠"),
        36.8556501, 127.180929,
    ),
    RestStop(
        "preview-3", "미리보기휴게소", Congestion.HIGH, null,
        listOf("주유소"), listOf("할리스커피"),
        36.78, 127.2,
    ),
)

@Composable
private fun PreviewFrame(state: UiState<List<RestStop>>) {
    MaterialTheme(colorScheme = RestStopColors) {
        Surface(color = Background) {
            NearbyContent(state = state, onRestStopClick = { }, onRetry = { })
        }
    }
}

@Preview(name = "1 성공", showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
private fun SuccessPreview() = PreviewFrame(UiState.Success(previewRestStops))

@Preview(name = "2 빈 목록", showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
private fun EmptyPreview() = PreviewFrame(UiState.Empty)

@Preview(name = "3 에러", showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
private fun ErrorPreview() = PreviewFrame(UiState.Error("가까운 휴게소를 불러오지 못했어요"))

// 실제 서버 확인용: 줄 번호 옆 ▶ → Run 'NearbyServerPreview' (에뮬레이터)
@Preview(name = "4 서버 (에뮬레이터에서 실행)", widthDp = 1280, heightDp = 800)
@Composable
private fun NearbyServerPreview() {
    MaterialTheme(colorScheme = RestStopColors) {
        Surface(color = Background) {
            NearbyRoute(onRestStopClick = { })
        }
    }
}
