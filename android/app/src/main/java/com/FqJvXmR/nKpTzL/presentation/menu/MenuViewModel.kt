package com.FqJvXmR.nKpTzL.presentation.menu

import androidx.lifecycle.ViewModel
import com.FqJvXmR.nKpTzL.domain.repository.LevelRepository
import com.FqJvXmR.nKpTzL.domain.usecase.GetProgressUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MenuViewModel(
    private val getProgress: GetProgressUseCase,
    private val levelRepository: LevelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MenuUiState(
            bestScore = 0,
            levelsCleared = 0,
            totalLevels = levelRepository.totalLevels(),
            currentLevel = 1
        )
    )
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val snapshot = getProgress()
        _uiState.value = MenuUiState(
            bestScore = snapshot.bestScore,
            levelsCleared = snapshot.levelsCleared,
            totalLevels = snapshot.totalLevels,
            currentLevel = snapshot.currentLevel
        )
    }
}
