package com.FqJvXmR.nKpTzL.domain.model

data class LevelBoard(
    val spec: LevelSpec,
    val puzzle: PuzzleState,
    val par: Int,
    val moveLimit: Int
)
