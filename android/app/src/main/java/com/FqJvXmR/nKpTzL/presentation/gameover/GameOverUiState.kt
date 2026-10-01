package com.FqJvXmR.nKpTzL.presentation.gameover

data class GameOverUiState(
    val win: Boolean,
    val levelIndex: Int,
    val nextLevel: Int,
    val score: Int,
    val stars: Int,
    val movesLeft: Int,
    val checksLeft: Int,
    val newBest: Boolean,
    val isLastLevel: Boolean
)
