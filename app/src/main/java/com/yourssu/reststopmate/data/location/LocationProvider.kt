package com.yourssu.reststopmate.data.location

import kotlinx.coroutines.flow.Flow

internal data class LatLng(val latitude: Double, val longitude: Double)

// "현재 위치를 계속 알려 준다"는 약속(interface)만 정한다. 어떻게 알아내는지는 구현체마다 다르다.
//  - 지금: FakeLocationProvider (고정 좌표)
//  - 나중: Pleos NaviHelper로 실제 차량 위치 (트랙 5). ViewModel은 이 약속만 알기 때문에 바꿀 필요가 없다.
internal interface LocationProvider {
    // 위치는 시간이 지나며 계속 바뀌므로 값 하나가 아니라 흐름(Flow)으로 준다.
    fun currentLocation(): Flow<LatLng>
}
