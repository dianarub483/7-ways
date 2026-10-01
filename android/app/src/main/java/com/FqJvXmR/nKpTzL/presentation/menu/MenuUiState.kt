package com.FqJvXmR.nKpTzL.presentation.menu

data class MenuUiState(
    val bestScore: Int,
    val levelsCleared: Int,
    val totalLevels: Int,
    val currentLevel: Int
) {
    val hasProgress: Boolean
        get() = bestScore > 0 || levelsCleared > 0
}
