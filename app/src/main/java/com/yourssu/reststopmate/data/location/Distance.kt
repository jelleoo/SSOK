package com.yourssu.reststopmate.data.location

import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

private const val EARTH_RADIUS_KM = 6371.0

// 두 좌표 사이의 직선거리(km). 하버사인(Haversine) 공식: 지구를 공으로 보고 표면을 따라 잰 거리.
// 실제 도로는 굽어 있으니 내비 거리보다 짧게 나온다. (계획서에서 정한 임시 방법)
// 확인용 손계산: 가짜 위치(37.0181, 127.143547) → 안성휴게소(37.013407, 127.1448001) ≈ 0.53km
internal fun distanceKm(from: LatLng, to: LatLng): Double {
    // 삼각함수(sin, cos)는 "도"가 아니라 "라디안"을 받는다. 위도/경도는 도 단위라서 바꿔 준다.
    val lat1 = Math.toRadians(from.latitude)
    val lat2 = Math.toRadians(to.latitude)
    val dLat = Math.toRadians(to.latitude - from.latitude)
    val dLng = Math.toRadians(to.longitude - from.longitude)

    val a = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLng / 2).pow(2)
    return 2 * EARTH_RADIUS_KM * asin(sqrt(a))
}
