package com.FqJvXmR.nKpTzL.domain.model

data class RoundResult(
    val levelIndex: Int,
    val win: Boolean,
    val score: Int,
    val stars: Int,
    val movesLeft: Int,
    val checksLeft: Int,
    val newBest: Boolean,
    val nextLevel: Int
)
