package com.FqJvXmR.nKpTzL.domain.model

data class ProgressSnapshot(
    val bestScore: Int,
    val levelsCleared: Int,
    val currentLevel: Int,
    val totalLevels: Int,
    val stars: Map<Int, Int>
)
