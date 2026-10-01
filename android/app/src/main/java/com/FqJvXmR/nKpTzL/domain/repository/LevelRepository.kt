package com.FqJvXmR.nKpTzL.domain.repository

import com.FqJvXmR.nKpTzL.domain.model.LevelSpec

interface LevelRepository {
    fun allSpecs(): List<LevelSpec>
    fun specFor(index: Int): LevelSpec
    fun totalLevels(): Int
}
