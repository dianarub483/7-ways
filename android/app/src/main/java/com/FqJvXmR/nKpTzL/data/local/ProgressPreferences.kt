package com.FqJvXmR.nKpTzL.data.local

import android.content.Context
import android.content.SharedPreferences

class ProgressPreferences(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)

    fun bestScore(): Int = preferences.getInt(KEY_BEST, 0)

    fun setBestScore(value: Int) {
        preferences.edit().putInt(KEY_BEST, value).apply()
    }

    fun currentLevel(): Int = preferences.getInt(KEY_CURRENT, 1)

    fun setCurrentLevel(value: Int) {
        preferences.edit().putInt(KEY_CURRENT, value).apply()
    }

    fun starsFor(level: Int): Int = preferences.getInt(KEY_STARS_PREFIX + level, 0)

    fun setStarsFor(level: Int, stars: Int) {
        preferences.edit().putInt(KEY_STARS_PREFIX + level, stars).apply()
    }

    fun clear() {
        preferences.edit().clear().apply()
    }

    private companion object {
        const val STORE_NAME = "seven_ways_progress"
        const val KEY_BEST = "best_score"
        const val KEY_CURRENT = "current_level"
        const val KEY_STARS_PREFIX = "stars_level_"
    }
}
