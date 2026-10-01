package com.FqJvXmR.nKpTzL.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.FqJvXmR.nKpTzL.core.config.GameConfig
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        SplashUiState(
            elapsedMs = 0L,
            totalMs = GameConfig.LOADER_DURATION_MS,
            handOffReady = false
        )
    )
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            val total = GameConfig.LOADER_DURATION_MS
            var elapsed = 0L
            while (elapsed < total) {
                delay(TICK_MS)
                elapsed += TICK_MS
                if (elapsed > total) {
                    elapsed = total
                }
                _uiState.value = _uiState.value.copy(elapsedMs = elapsed)
            }
            _uiState.value = _uiState.value.copy(elapsedMs = total, handOffReady = true)
        }
    }

    fun consumeHandOff() {
        if (_uiState.value.handOffReady) {
            _uiState.value = _uiState.value.copy(handOffReady = false)
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        timerJob = null
        super.onCleared()
    }

    private companion object {
        const val TICK_MS = 250L
    }
}
