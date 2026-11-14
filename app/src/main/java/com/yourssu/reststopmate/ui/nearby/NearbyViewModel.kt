package com.yourssu.reststopmate.ui.nearby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yourssu.reststopmate.data.RestStop
import com.yourssu.reststopmate.data.RestStopRepository
import com.yourssu.reststopmate.data.location.FakeLocationProvider
import com.yourssu.reststopmate.data.location.LatLng
import com.yourssu.reststopmate.data.location.LocationProvider
import com.yourssu.reststopmate.ui.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 위치가 올 때마다 서버에 "내 앞 휴게소"를 묻고, 결과를 UiState로 화면에 알린다.
// ViewModel은 LocationProvider(약속)만 안다. 가짜 위치든 실제 위치든 상관없다.
internal class NearbyViewModel(
    private val locationProvider: LocationProvider,
    private val repository: RestStopRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<List<RestStop>>>(UiState.Loading)
    val state: StateFlow<UiState<List<RestStop>>> = _state.asStateFlow()

    // 마지막으로 받은 차량 위치. ③ 주행 중 홈에서 휴게소까지 거리를 계산할 때 쓴다.
    private val _location = MutableStateFlow<LatLng?>(null)
    val location: StateFlow<LatLng?> = _location.asStateFlow()

    private var job: Job? = null

    init {
        load()
    }

    fun load() {
        job?.cancel() // "다시 시도"를 여러 번 눌러도 요청이 겹치지 않게 이전 것을 멈춘다
        _state.value = UiState.Loading
        job = viewModelScope.launch {
            try {
                // collect: Flow에서 값이 올 때마다 { } 안을 실행한다
                locationProvider.currentLocation().collect { location ->
                    _location.value = location
                    val restStops = repository.getNextRestStops(location)
                    _state.value = if (restStops.isEmpty())
                        UiState.Empty
                    else
                        UiState.Success(restStops)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = UiState.Error("가까운 휴게소를 불러오지 못했어요")
            }
        }
    }

    companion object {
        // 지금은 가짜 위치. 트랙 5에서 Pleos 위치 구현체로 이 기본값만 바꾼다.
        fun factory(
            locationProvider: LocationProvider = FakeLocationProvider(),
            repository: RestStopRepository = RestStopRepository(),
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                require(modelClass.isAssignableFrom(NearbyViewModel::class.java))
                return NearbyViewModel(locationProvider, repository) as T
            }
        }
    }
}
