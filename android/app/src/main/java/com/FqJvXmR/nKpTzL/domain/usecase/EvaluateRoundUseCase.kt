package com.FqJvXmR.nKpTzL.domain.usecase

import com.FqJvXmR.nKpTzL.domain.model.RoundResult

class EvaluateRoundUseCase {

    operator fun invoke(
        levelIndex: Int,
        win: Boolean,
        movesLeft: Int,
        checksLeft: Int,
        par: Int,
        bestScore: Int,
        totalLevels: Int
    ): RoundResult {
        val score = movesLeft * MOVE_POINTS +
            checksLeft * CHECK_POINTS +
            levelIndex * LEVEL_POINTS +
            if (win) WIN_BONUS else 0
        val stars = when {
            !win -> 0
            movesLeft >= par / 2 && movesLeft >= 3 -> 3
            movesLeft >= 2 -> 2
            else -> 1
        }
        val next = if (win) {
            if (levelIndex < totalLevels) levelIndex + 1 else levelIndex
        } else {
            levelIndex
        }
        return RoundResult(
            levelIndex = levelIndex,
            win = win,
            score = score,
            stars = stars,
            movesLeft = movesLeft,
            checksLeft = checksLeft,
            newBest = score > bestScore,
            nextLevel = next
        )
    }

    private companion object {
        const val MOVE_POINTS = 50
        const val CHECK_POINTS = 150
        const val LEVEL_POINTS = 25
        const val WIN_BONUS = 300
    }
}
