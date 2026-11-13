package com.yourssu.reststopmate.data

import androidx.annotation.DrawableRes

// 화면에서 쓰는 메뉴 모델. 서버 응답(DTO)은 나중에 Repository에서 이 모양으로 바꿔 넣는다.
// 서버 메뉴에는 id가 없어서, 목록 key로 쓸 id는 앱에서 만든다.
internal data class Menu(
    val id: String,
    val name: String,
    val description: String?,
    val priceWon: Int,
    @DrawableRes val imageRes: Int,
)
