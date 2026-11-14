package com.yourssu.reststopmate.data

// 화면에서 쓰는 휴게소 모델. 서버 RestStopDto는 RestStopRepository에서 이 모양으로 바꾼다.
internal data class RestStop(
    val id: String,
    val name: String,
    val congestion: Congestion,
    val featuredMenu: FeaturedMenu?,
    val tags: List<String>,     // 편의시설: 주유소, LPG 충전소, 전기차 충전소
    val brands: List<String>,   // 입점 브랜드: 할리스커피, 던킨도너츠 ...
    val latitude: Double,
    val longitude: Double,
)

internal data class FeaturedMenu(
    val name: String,
    val description: String?,
    val imageUrl: String?,
)

// 서버 congestion 값(LOW/MEDIUM/HIGH)과 화면 문구
internal enum class Congestion(val label: String) {
    LOW("여유로움"),
    MEDIUM("적당함"),
    HIGH("혼잡함"), // Figma에 HIGH 문구가 없어 임시. 팀 확인 필요
    UNKNOWN("정보 없음"),
}
