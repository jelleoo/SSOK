package com.yourssu.reststopmate.ui.menu

import androidx.compose.foundation.content.contentReceiver
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourssu.reststopmate.R
import com.yourssu.reststopmate.data.Menu
import com.yourssu.reststopmate.ui.UiState
import com.yourssu.reststopmate.ui.theme.Background
import com.yourssu.reststopmate.ui.theme.Muted
import com.yourssu.reststopmate.ui.theme.RestStopColors

// 바깥: ViewModel을 만들고 상태를 구독해서 안쪽(MenuListContent)에 넘긴다.
// 나중에 MainTabsScreen에서는 이 함수를 부른다.
@Composable
internal fun MenuListRoute(
    restStopId: String,
    restStopName: String,
    onBack: () -> Unit,
) {
    // key = restStopId: 다른 휴게소를 열면 새 ViewModel을 만든다
    val viewModel: MenuListViewModel = viewModel(
        key = restStopId,
        factory = MenuListViewModel.factory(restStopId),
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    MenuListContent(
        restStopName = restStopName,
        state = state,
        onBack = onBack,
        onRetry = viewModel::load,
    )
}

// 안쪽: 상태만 받아서 그린다. ViewModel이 없으니 Preview로 4가지 상태를 모두 볼 수 있다.
@Composable
internal fun MenuListContent(
    restStopName: String,
    state: UiState<List<Menu>>,
    onBack: () -> Unit,
    onRetry: () -> Unit,
) {
    when (state) {
        is UiState.Success -> MenuListScreen(
            restStopName = restStopName,
            menus = state.data,
            onBack = onBack,
        )

        UiState.Loading -> Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        )
        {
            CircularProgressIndicator()
        }

        UiState.Empty -> Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        )
        {
            Text("등록된 메뉴가 없어요",
                color = Muted,
                fontSize = 20.sp)
        }
        is UiState.Error -> Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(state.message);
                Button(onClick = onRetry) {
                    Text("다시 시도하세요")
                }
            }
        }
    }
}

private val previewMenus = listOf(
    Menu("m1", "매운철판돈까스", "매콤한 돈까스와 치즈의 조합", 9900, R.drawable.pork_cutlet),
    Menu("m2", "해물라면", null, 6500, R.drawable.seafood_ramen),
)

@Composable
private fun PreviewFrame(state: UiState<List<Menu>>) {
    MaterialTheme(colorScheme = RestStopColors) {
        Surface(color = Background) {
            MenuListContent(restStopName = "화성휴게소", state = state, onBack = { }, onRetry = { })
        }
    }
}

@Preview(name = "1 로딩", showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
private fun LoadingPreview() = PreviewFrame(UiState.Loading)

@Preview(name = "2 성공", showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
private fun SuccessPreview() = PreviewFrame(UiState.Success(previewMenus))

@Preview(name = "3 빈 목록", showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
private fun EmptyPreview() = PreviewFrame(UiState.Empty)

@Preview(name = "4 에러", showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
private fun ErrorPreview() = PreviewFrame(UiState.Error("메뉴 정보를 불러오지 못했어요"))

// 실제 서버 확인용. Android Studio 화면에서는 서버에 연결되지 않으니,
// 줄 번호 옆 ▶ → Run 'ServerPreview'로 에뮬레이터에서 실행한다.
@Preview(name = "5 서버 (에뮬레이터에서 실행)", widthDp = 1280, heightDp = 800)
@Composable
private fun ServerPreview() {
    MaterialTheme(colorScheme = RestStopColors) {
        Surface(color = Background) {
            MenuListRoute(restStopId = "anseong-busan", restStopName = "안성휴게소", onBack = { })
        }
    }
}
