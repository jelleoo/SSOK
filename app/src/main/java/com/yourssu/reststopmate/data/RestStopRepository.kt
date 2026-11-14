package com.yourssu.reststopmate.data

import com.yourssu.reststopmate.R
import com.yourssu.reststopmate.data.remote.NetworkModule
import com.yourssu.reststopmate.data.remote.RestStopApi

// 서버에서 받은 DTO를 화면용 모델(Menu)로 바꿔서 넘김
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
}
