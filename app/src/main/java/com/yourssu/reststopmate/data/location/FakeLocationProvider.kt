package com.yourssu.reststopmate.data.location

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

// 시연/개발용: 항상 같은 좌표(경부고속도로 안성 부근, 서버 문서의 예시 좌표)를 알려 준다.
internal class FakeLocationProvider : LocationProvider {

    override fun currentLocation(): Flow<LatLng> = flowOf(LatLng(latitude = 37.0181, longitude = 127.143547))
}
