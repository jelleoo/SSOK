package com.yourssu.reststopmate.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yourssu.reststopmate.data.SessionRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class SessionStatus { Loading, Login, Home, ReadError }

data class SessionUiState(
    val status: SessionStatus = SessionStatus.Loading,
    val saving: Boolean = false,
    val error: String? = null,
)

class SessionViewModel(private val repository: SessionRepository) : ViewModel() {
    private val mutableState = MutableStateFlow(SessionUiState())
    val state = mutableState.asStateFlow()
    private var observation: Job? = null

    init { observeSession() }

    fun observeSession() {
        observation?.cancel()
        mutableState.value = SessionUiState()
        observation = viewModelScope.launch {
            try {
                repository.isGuest.collect { guest ->
                    mutableState.value = SessionUiState(
                        status = if (guest) SessionStatus.Home else SessionStatus.Login,
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                mutableState.value = SessionUiState(
                    SessionStatus.ReadError,
                    error = "이용 상태를 불러오지 못했어요. 다시 시도해 주세요.",
                )
            }
        }
    }

    fun startGuest() {
        if (state.value.status != SessionStatus.Login || state.value.saving) return
        mutableState.update { it.copy(saving = true, error = null) }
        viewModelScope.launch {
            try {
                repository.startGuest()
                // Home is shown only after the persisted value is emitted by DataStore.
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                mutableState.update {
                    it.copy(saving = false, error = "저장하지 못했어요. 다시 시도해 주세요.")
                }
            }
        }
    }

    companion object {
        fun factory(repository: SessionRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    require(modelClass.isAssignableFrom(SessionViewModel::class.java))
                    return SessionViewModel(repository) as T
                }
            }
    }
}
