package com.yourssu.reststopmate.data

import com.yourssu.reststopmate.R
import com.yourssu.reststopmate.data.location.LatLng
import com.yourssu.reststopmate.data.remote.NetworkModule
import com.yourssu.reststopmate.data.remote.RestStopApi
import com.yourssu.reststopmate.data.remote.dto.FeaturedMenuDto
import com.yourssu.reststopmate.data.remote.dto.RestStopDto

// 서버에서 받은 DTO를 화면용 모델(Menu, RestStop)로 바꿔서 넘김
internal class RestStopRepository(
    private val api: RestStopApi = NetworkModule.api,
) {
    suspend fun getMenus(restStopId: String): List<Menu> {
        val response = api.getMenus(restStopId)
        return response.menus.mapIndexed { index, dto ->
            Menu(
                id = "$restStopId-$index",
                name = dto.name,
                description = dto.description,
                priceWon = dto.priceWon,
                imageRes = R.drawable.pork_cutlet,
                imageUrl = NetworkModule.imageUrl(dto.imageUrl),
            )
        }
    }

    // 차량 앞에 있는 휴게소들 (가까운 순서). 지원 구간 밖이면 서버가 빈 목록을 준다.
    suspend fun getNextRestStops(location: LatLng): List<RestStop> {
        val response = api.getNext(lat = location.latitude, lng = location.longitude)
        return response.map { dto -> toRestStop(dto) }
    }

    private fun toRestStop(dto: RestStopDto): RestStop = RestStop(
        id = dto.id,
        name = dto.name,
        congestion = toCongestion(dto.congestion),
        featuredMenu = dto.featuredMenu?.let { toFeaturedMenu(it) },
        tags = dto.tags,
        brands = dto.brands,
        latitude = dto.latitude ?: 0.0,
        longitude = dto.longitude ?: 0.0,
    )

    // 대표 메뉴 이름이 없으면 카드에 보여 줄 게 없으니 null로 둔다.
    private fun toFeaturedMenu(dto: FeaturedMenuDto): FeaturedMenu? {
        val name = dto.name ?: return null
        return FeaturedMenu(
            name = name,
            description = dto.description,
            imageUrl = NetworkModule.imageUrl(dto.imageUrl),
        )
    }

    private fun toCongestion(value: String?): Congestion {
        return when (value) {
            "LOW" -> Congestion.LOW
            "MEDIUM" -> Congestion.MEDIUM
            "HIGH" -> Congestion.HIGH
            else -> Congestion.UNKNOWN
        }
    }
}
