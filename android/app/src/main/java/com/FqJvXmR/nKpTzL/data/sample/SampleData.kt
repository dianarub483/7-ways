package com.FqJvXmR.nKpTzL.data.sample

import com.FqJvXmR.nKpTzL.domain.model.LevelSpec

object SampleData {

    const val SET_BRONZE = "BRONZE REELS"
    const val SET_GOLDEN = "GOLDEN STREAK"
    const val SET_CRIMSON = "CRIMSON OVERLOAD"

    val SET_NAMES: List<String> = listOf(SET_BRONZE, SET_GOLDEN, SET_CRIMSON)

    val LEVEL_SPECS: List<LevelSpec> = listOf(
        LevelSpec(1, SET_BRONZE, 7001L, 4, 2, 3),
        LevelSpec(2, SET_BRONZE, 7002L, 4, 3, 3),
        LevelSpec(3, SET_BRONZE, 7003L, 4, 3, 3),
        LevelSpec(4, SET_BRONZE, 7004L, 4, 4, 3),
        LevelSpec(5, SET_GOLDEN, 7005L, 5, 3, 3),
        LevelSpec(6, SET_GOLDEN, 7006L, 5, 4, 3),
        LevelSpec(7, SET_GOLDEN, 7007L, 5, 4, 3),
        LevelSpec(8, SET_GOLDEN, 7008L, 5, 5, 3),
        LevelSpec(9, SET_CRIMSON, 7009L, 5, 5, 3),
        LevelSpec(10, SET_CRIMSON, 7010L, 5, 5, 3),
        LevelSpec(11, SET_CRIMSON, 7011L, 5, 6, 3),
        LevelSpec(12, SET_CRIMSON, 7012L, 5, 6, 3)
    )

    fun specsForSet(setName: String): List<LevelSpec> = LEVEL_SPECS.filter { it.setName == setName }
}
