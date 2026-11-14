package com.yourssu.reststopmate.data.remote

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

// Retrofit 객체를 앱 전체에서 한 번만 만든다.
internal object NetworkModule {
    const val BASE_URL = "https://3.34.5.244/"

    // 서버가 앱이 모르는 필드를 추가해도 앱이 죽지 않게 한다.
    private val json = Json { ignoreUnknownKeys = true }

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
        .build()

    val api: RestStopApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(RestStopApi::class.java)

    // 서버 이미지 주소는 상대 경로라서 서버 주소를 앞에 붙인다.
    fun imageUrl(path: String?): String? = path?.let { BASE_URL.trimEnd('/') + it }
}
