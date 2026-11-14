package com.yourssu.reststopmate.ui.nearby

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourssu.reststopmate.data.Congestion
import com.yourssu.reststopmate.data.FeaturedMenu
import com.yourssu.reststopmate.data.RestStop
import com.yourssu.reststopmate.data.location.LatLng
import com.yourssu.reststopmate.data.location.distanceKm
import com.yourssu.reststopmate.ui.UiState
import com.yourssu.reststopmate.ui.components.DistanceProgress
import com.yourssu.reststopmate.ui.components.RestAreaCard
import com.yourssu.reststopmate.ui.theme.Background
import com.yourssu.reststopmate.ui.theme.Muted
import com.yourssu.reststopmate.ui.theme.RestStopColors

// 게이지가 꽉 차는 기준 거리. 휴게소에 가까워질수록 게이지가 찬다.
// 임시값: 팀(디자인)과 기준을 정하면 이 숫자만 바꾼다. (예: 이전 휴게소~다음 휴게소 거리)
private const val GAUGE_FULL_KM = 10.0

// 바깥: ②와 같은 NearbyViewModel을 쓴다 (둘 다 /next 결과를 보여 주므로). 나중에 HomeScreen(좁은 화면)에서 부른다.
@Composable
internal fun DrivingHomeRoute(
    onRestStopClick: (restStopId: String) -> Unit,
) {
    val viewModel: NearbyViewModel = viewModel(factory = NearbyViewModel.factory())
    val state by viewModel.state.collectAsStateWithLifecycle()
    val location by viewModel.location.collectAsStateWithLifecycle()
    DrivingHomeContent(state, location, onRestStopClick, onRetry = viewModel::load)
}

@Composable
internal fun DrivingHomeContent(
    state: UiState<List<RestStop>>,
    location: LatLng?,
    onRestStopClick: (restStopId: String) -> Unit,
    onRetry: () -> Unit,
) {
    when (state) {
        // Success면 목록이 비어 있지 않다 (빈 목록은 Empty). 그래서 first()가 안전하다.
        is UiState.Success -> if (location != null) {
            DrivingHomeScreen(state.data.first(), location, onRestStopClick)
        }
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

// ③ 주행 중 홈 (세로 화면): 왼쪽 [휴게소까지 + 거리 + 게이지] | 오른쪽 [가장 가까운 휴게소 카드]
@Composable
internal fun DrivingHomeScreen(
    restStop: RestStop,
    location: LatLng,
    onRestStopClick: (restStopId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val km = distanceKm(from = location,
        to = LatLng(restStop.latitude, restStop.longitude))

    val distanceText = "%.1fkm".format(km)

    val fraction = (1 - km / GAUGE_FULL_KM).toFloat()

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Column(
            modifier = Modifier
                .width(100.dp)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("휴게소까지", fontSize = 18.sp, color = Muted)
            Text(distanceText, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            DistanceProgress(fraction = fraction, modifier = Modifier.weight(1f))
        }

        RestAreaCard(
            restStop = restStop,
            onClick = { onRestStopClick(restStop.id) },
            onAdd = { },
            modifier = Modifier.weight(1f),
        )
    }
}

private val previewRestStop = RestStop(
    "anseong-busan", "안성휴게소", Congestion.LOW, FeaturedMenu("안성국밥", null, null),
    listOf("주유소", "LPG 충전소", "전기차 충전소"), listOf("할리스커피", "로띠번", "파스쿠찌"),
    37.013407, 127.1448001,
)

@Composable
private fun PreviewFrame(location: LatLng) {
    MaterialTheme(colorScheme = RestStopColors) {
        Surface(color = Background) {
            DrivingHomeScreen(restStop = previewRestStop, location = location, onRestStopClick = { })
        }
    }
}

// 가짜 위치(FakeLocationProvider와 같은 좌표) → 안성휴게소 약 0.5km: 게이지 거의 꽉 참
@Preview(name = "1 가까움", showBackground = true, widthDp = 420, heightDp = 860)
@Composable
private fun NearPreview() = PreviewFrame(LatLng(37.0181, 127.143547))

// 북쪽으로 약 7km 떨어진 위치: 게이지 30% 정도
@Preview(name = "2 멂", showBackground = true, widthDp = 420, heightDp = 860)
@Composable
private fun FarPreview() = PreviewFrame(LatLng(37.0768, 127.1448))

// 실제 서버 확인용: 줄 번호 옆 ▶ → Run 'DrivingHomeServerPreview' (에뮬레이터)
@Preview(name = "3 서버 (에뮬레이터에서 실행)", widthDp = 420, heightDp = 860)
@Composable
private fun DrivingHomeServerPreview() {
    MaterialTheme(colorScheme = RestStopColors) {
        Surface(color = Background) {
            DrivingHomeRoute(onRestStopClick = { })
        }
    }
}
