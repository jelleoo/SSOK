package com.yourssu.reststopmate.data.mock

import com.yourssu.reststopmate.R

internal data class RestAreaRecord(val name: String, val visit: String)
internal data class MealRecord(val id: String, val restArea: String, val menu: String, val image: Int)

internal val mockRestAreas = listOf(
    RestAreaRecord("김천휴게소", "3일 전 방문"),
    RestAreaRecord("매봉휴게소", "2주 전 방문"),
    RestAreaRecord("문산휴게소", "방문 기록 없음"),
)
internal val mockMeals = listOf(
    MealRecord("meal-1", "김천휴게소", "매운철판돈까스", R.drawable.pork_cutlet),
    MealRecord("meal-2", "매봉휴게소", "해물라면", R.drawable.seafood_ramen),
    MealRecord("meal-3", "김천휴게소", "매운철판돈까스", R.drawable.pork_cutlet),
    MealRecord("meal-4", "매봉휴게소", "해물라면", R.drawable.seafood_ramen),
)
