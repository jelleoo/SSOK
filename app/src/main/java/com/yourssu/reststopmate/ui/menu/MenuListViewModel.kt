package com.yourssu.reststopmate.ui.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yourssu.reststopmate.data.Menu
import com.yourssu.reststopmate.data.RestStopRepository
import com.yourssu.reststopmate.ui.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 화면이 회전해도 ViewModel은 남아 있어서, 서버 요청을 다시 보내지 않는다.
internal class MenuListViewModel(
    private val restStopId: String,
    private val repository: RestStopRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<List<Menu>>>(UiState.Loading)
    val state: StateFlow<UiState<List<Menu>>> = _state.asStateFlow()

    init {
        load()
    }

    // 처음 열 때, 그리고 에러 화면의 "다시 시도" 버튼에서 부른다.
    fun load() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            try {
                val menus = repository.getMenus(restStopId)
                _state.value = if (menus.isEmpty())
                    UiState.Empty
                else
                    UiState.Success(menus)
            } catch (e: CancellationException) {
                throw e // 화면을 떠나 요청이 취소된 것은 에러가 아니다. 그대로 넘긴다.
            } catch (e: Exception) {
                _state.value = UiState.Error("메뉴 정보를 불러오지 못했어요")
            }
        }
    }

    companion object {
        fun factory(
            restStopId: String,
            repository: RestStopRepository = RestStopRepository(),
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                require(modelClass.isAssignableFrom(MenuListViewModel::class.java))
                return MenuListViewModel(restStopId, repository) as T
            }
        }
    }
}
