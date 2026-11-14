package com.yourssu.reststopmate.data.remote

import com.yourssu.reststopmate.data.remote.dto.MenuListDto
import com.yourssu.reststopmate.data.remote.dto.RestStopDto
import retrofit2.http.GET
import retrofit2.http.Path

internal interface RestStopApi {

    // GET /api/rest-stops → 전체 휴게소 목록
    @GET("api/rest-stops")
    suspend fun getRestStops(): List<RestStopDto>

    // GET /api/rest-stops/{restStopId}/menus → 휴게소 메뉴 목록
    @GET("api/rest-stops/{restStopId}/menus")
    suspend fun getMenus(
        @Path("restStopId") restStopId: String,
    ): MenuListDto
}
