package com.FqJvXmR.nKpTzL.domain.repository

import com.FqJvXmR.nKpTzL.domain.model.ProgressSnapshot
import com.FqJvXmR.nKpTzL.domain.model.RoundResult

interface ProgressRepository {
    fun snapshot(): ProgressSnapshot
    fun store(result: RoundResult)
    fun bestScore(): Int
    fun currentLevel(): Int
    fun setCurrentLevel(index: Int)
    fun reset()
}
