package com.FqJvXmR.nKpTzL.data.repository

import com.FqJvXmR.nKpTzL.data.local.ProgressPreferences
import com.FqJvXmR.nKpTzL.domain.model.ProgressSnapshot
import com.FqJvXmR.nKpTzL.domain.model.RoundResult
import com.FqJvXmR.nKpTzL.domain.repository.LevelRepository
import com.FqJvXmR.nKpTzL.domain.repository.ProgressRepository

class ProgressRepositoryImpl(
    private val preferences: ProgressPreferences,
    private val levelRepository: LevelRepository
) : ProgressRepository {

    override fun snapshot(): ProgressSnapshot {
        val total = levelRepository.totalLevels()
        val stars = mutableMapOf<Int, Int>()
        var cleared = 0
        for (level in 1..total) {
            val earned = preferences.starsFor(level)
            stars[level] = earned
            if (earned > 0) {
                cleared++
            }
        }
        return ProgressSnapshot(
            bestScore = preferences.bestScore(),
            levelsCleared = cleared,
            currentLevel = preferences.currentLevel().coerceIn(1, total),
            totalLevels = total,
            stars = stars
        )
    }

    override fun store(result: RoundResult) {
        if (result.score > preferences.bestScore()) {
            preferences.setBestScore(result.score)
        }
        if (result.win) {
            val previous = preferences.starsFor(result.levelIndex)
            if (result.stars > previous) {
                preferences.setStarsFor(result.levelIndex, result.stars)
            }
            preferences.setCurrentLevel(result.nextLevel)
        }
    }

    override fun bestScore(): Int = preferences.bestScore()

    override fun currentLevel(): Int =
        preferences.currentLevel().coerceIn(1, levelRepository.totalLevels())

    override fun setCurrentLevel(index: Int) {
        preferences.setCurrentLevel(index.coerceIn(1, levelRepository.totalLevels()))
    }

    override fun reset() {
        preferences.clear()
    }
}
