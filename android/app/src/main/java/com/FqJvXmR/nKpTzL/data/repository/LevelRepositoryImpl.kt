package com.FqJvXmR.nKpTzL.data.repository

import com.FqJvXmR.nKpTzL.data.sample.SampleData
import com.FqJvXmR.nKpTzL.domain.model.LevelSpec
import com.FqJvXmR.nKpTzL.domain.repository.LevelRepository

class LevelRepositoryImpl : LevelRepository {

    override fun allSpecs(): List<LevelSpec> = SampleData.LEVEL_SPECS

    override fun specFor(index: Int): LevelSpec {
        val specs = SampleData.LEVEL_SPECS
        val safe = index.coerceIn(1, specs.size)
        return specs[safe - 1]
    }

    override fun totalLevels(): Int = SampleData.LEVEL_SPECS.size
}
