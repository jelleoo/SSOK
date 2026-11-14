package com.yourssu.reststopmate.ui.home

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import com.yourssu.reststopmate.data.RestStop
import com.yourssu.reststopmate.ui.nearby.DrivingHomeRoute
import com.yourssu.reststopmate.ui.nearby.NearbyRoute

// Home 탭: 화면 폭에 따라 ② 가까운 휴게소(넓음) 또는 ③ 주행 중 홈(좁음)을 보여 준다.
// compact는 MainTabsScreen이 하단/좌측 메뉴를 고를 때 쓰는 기준(폭 700dp)과 같은 값을 받는다.
@Composable
internal fun HomeScreen(
    compact: Boolean,
    nearbyListState: LazyListState,
    onRestStopClick: (restStop: RestStop) -> Unit,
    onSearch: () -> Unit, // 검색 바로가기. ②③ Figma에 검색 버튼이 없어 지금은 쓰지 않는다. 위치는 팀 확인 필요
) {
    if (compact){
        DrivingHomeRoute(onRestStopClick = onRestStopClick)
    }
    else{
        NearbyRoute(onRestStopClick = onRestStopClick,
            listState = nearbyListState)
    }
}
