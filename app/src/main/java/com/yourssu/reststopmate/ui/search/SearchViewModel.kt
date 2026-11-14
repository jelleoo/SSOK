package com.yourssu.reststopmate.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.reststopmate.data.search.MenuItem
import com.yourssu.reststopmate.data.search.FeaturedMenu
import com.yourssu.reststopmate.data.search.RestStop
import com.yourssu.reststopmate.data.search.RestStopRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal data class SearchState(
    val query: String = "",
    val stops: List<RestStop> = emptyList(),
    val recent: List<RestStop> = emptyList(),
    val loading: Boolean = true,
    val historyLoading: Boolean = true,
    val networkError: Boolean = false,
    val historyError: Boolean = false,
    val saving: Boolean = false,
    val message: String? = null,
    val selectedStop: RestStop? = null,
    val featuredMenu: FeaturedMenu? = null,
    val menuLoading: Boolean = false,
    val menuError: Boolean = false,
    val showAllMenus: Boolean = false,
    val allMenus: List<MenuItem> = emptyList(),
    val allMenusLoading: Boolean = false,
    val allMenusError: Boolean = false,
) {
    val results: List<RestStop>
        get() {
            val needle = query.filterNot(Char::isWhitespace)
            return stops.filter { it.name.filterNot(Char::isWhitespace).contains(needle, ignoreCase = true) }
        }
}

internal class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RestStopRepository(application)
    private val mutableState = MutableStateFlow(SearchState())
    val state = mutableState.asStateFlow()
    private var fetchJob: Job? = null
    private var historyJob: Job? = null
    private var menuJob: Job? = null
    private var allMenusJob: Job? = null

    init {
        load()
        observeHistory()
    }

    fun setQuery(query: String) { mutableState.update { it.copy(query = query) } }
    fun dismissMessage() { mutableState.update { it.copy(message = null) } }

    fun load() {
        if (fetchJob?.isActive == true) return
        fetchJob = viewModelScope.launch {
            mutableState.update { it.copy(loading = true, networkError = false) }
            try {
                val stops = repository.fetchRestStops()
                mutableState.update { it.copy(stops = stops, loading = false) }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                mutableState.update { it.copy(loading = false, networkError = true) }
            }
        }
    }

    fun observeHistory() {
        historyJob?.cancel()
        historyJob = viewModelScope.launch {
            mutableState.update { it.copy(historyLoading = true, historyError = false) }
            repository.recent.catch { error ->
                if (error is CancellationException) throw error
                mutableState.update { it.copy(historyLoading = false, historyError = true) }
            }.collect { recent ->
                mutableState.update { it.copy(recent = recent, historyLoading = false, historyError = false) }
            }
        }
    }

    fun closeMenu() {
        menuJob?.cancel()
        allMenusJob?.cancel()
        mutableState.update { it.copy(selectedStop = null, featuredMenu = null, menuLoading = false, menuError = false, showAllMenus = false, allMenus = emptyList(), allMenusLoading = false, allMenusError = false) }
    }

    fun backToFeatured() { mutableState.update { it.copy(showAllMenus = false) } }

    fun showAllMenus() {
        val stop = mutableState.value.selectedStop ?: return
        allMenusJob?.cancel()
        mutableState.update { it.copy(showAllMenus = true, allMenus = emptyList(), allMenusLoading = true, allMenusError = false) }
        allMenusJob = viewModelScope.launch {
            try {
                val menus = repository.fetchMenus(stop.id)
                mutableState.update {
                    if (it.selectedStop?.id == stop.id) it.copy(allMenus = menus, allMenusLoading = false) else it
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                mutableState.update {
                    if (it.selectedStop?.id == stop.id) it.copy(allMenusLoading = false, allMenusError = true) else it
                }
            }
        }
    }

    fun retryMenu() { mutableState.value.selectedStop?.let(::openMenu) }

    private fun openMenu(stop: RestStop) {
        menuJob?.cancel()
        allMenusJob?.cancel()
        mutableState.update { it.copy(selectedStop = stop, featuredMenu = null, menuLoading = true, menuError = false, showAllMenus = false, allMenus = emptyList(), allMenusLoading = false, allMenusError = false) }
        menuJob = viewModelScope.launch {
            try {
                val menu = repository.fetchFeaturedMenu(stop.id)
                mutableState.update {
                    if (it.selectedStop?.id == stop.id) it.copy(featuredMenu = menu, menuLoading = false) else it
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                mutableState.update {
                    if (it.selectedStop?.id == stop.id) it.copy(menuLoading = false, menuError = true) else it
                }
            }
        }
    }

    fun select(restStop: RestStop, recordHistory: Boolean) {
        openMenu(restStop)
        if (!recordHistory) return
        if (mutableState.value.saving) return
        mutableState.update { it.copy(saving = true) }
        viewModelScope.launch {
            try {
                repository.rememberSelection(restStop)
                mutableState.update { it.copy(message = "최근 검색 항목에 저장했어요.") }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                mutableState.update { it.copy(message = "검색 기록을 저장하지 못했어요. 다시 선택해 주세요.") }
            } finally {
                mutableState.update { it.copy(saving = false) }
            }
        }
    }
}
