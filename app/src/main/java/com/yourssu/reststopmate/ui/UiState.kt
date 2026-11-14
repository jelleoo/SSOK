package com.yourssu.reststopmate.ui

// 서버에서 데이터를 받아 오는 화면이 가질 수 있는 4가지 상태
internal sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data object Empty : UiState<Nothing>
    data class Error(val message: String) : UiState<Nothing>
}
