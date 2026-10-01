package com.FqJvXmR.nKpTzL.data.local

import android.content.Context
import android.content.SharedPreferences

class SettingsPreferences(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)

    fun soundCues(): Boolean = preferences.getBoolean(KEY_SOUND, true)

    fun haptics(): Boolean = preferences.getBoolean(KEY_HAPTICS, true)

    fun highContrast(): Boolean = preferences.getBoolean(KEY_CONTRAST, false)

    fun store(sound: Boolean, haptics: Boolean, contrast: Boolean) {
        preferences.edit()
            .putBoolean(KEY_SOUND, sound)
            .putBoolean(KEY_HAPTICS, haptics)
            .putBoolean(KEY_CONTRAST, contrast)
            .apply()
    }

    private companion object {
        const val STORE_NAME = "seven_ways_settings"
        const val KEY_SOUND = "sound_cues"
        const val KEY_HAPTICS = "haptics"
        const val KEY_CONTRAST = "high_contrast"
    }
}
