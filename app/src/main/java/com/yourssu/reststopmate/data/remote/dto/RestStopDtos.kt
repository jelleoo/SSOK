package com.yourssu.reststopmate.data.remote.dto

import kotlinx.serialization.Serializable

// 서버 JSON 모양 그대로의 클래스. 서버가 값을 빼고 보낼 수 있는 필드는 ?와 기본값을 둔다.

@Serializable
internal data class RestStopDto(
    val id: String,
    val name: String,
    val highway: String? = null,
    val direction: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val routeOrder: Int? = null,
    val congestion: String? = null, // LOW / MEDIUM / HIGH
    val tags: List<String> = emptyList(),
    val brands: List<String> = emptyList(),
    val featuredMenu: FeaturedMenuDto? = null,
    val demo: Boolean = false,
)

@Serializable
internal data class FeaturedMenuDto(
    val name: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
)

@Serializable
internal data class MenuListDto(
    val restStopId: String,
    val menus: List<MenuDto> = emptyList(),
)

@Serializable
internal data class MenuDto(
    val name: String,
    val description: String? = null,
    val priceWon: Int,
    val imageUrl: String? = null,
)
