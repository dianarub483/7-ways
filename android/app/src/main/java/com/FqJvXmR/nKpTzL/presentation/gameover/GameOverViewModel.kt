package com.FqJvXmR.nKpTzL.presentation.gameover

import androidx.lifecycle.ViewModel
import com.FqJvXmR.nKpTzL.domain.repository.LevelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameOverViewModel(private val levelRepository: LevelRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<GameOverUiState?>(null)
    val uiState: StateFlow<GameOverUiState?> = _uiState.asStateFlow()

    fun bind(
        win: Boolean,
        levelIndex: Int,
        nextLevel: Int,
        score: Int,
        stars: Int,
        movesLeft: Int,
        checksLeft: Int,
        newBest: Boolean
    ) {
        if (_uiState.value != null) {
            return
        }
        val total = levelRepository.totalLevels()
        _uiState.value = GameOverUiState(
            win = win,
            levelIndex = levelIndex,
            nextLevel = nextLevel.coerceIn(1, total),
            score = score,
            stars = stars,
            movesLeft = movesLeft,
            checksLeft = checksLeft,
            newBest = newBest,
            isLastLevel = levelIndex >= total
        )
    }
}
