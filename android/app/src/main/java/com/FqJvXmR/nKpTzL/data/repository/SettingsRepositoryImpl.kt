package com.FqJvXmR.nKpTzL.data.repository

import com.FqJvXmR.nKpTzL.data.local.SettingsPreferences
import com.FqJvXmR.nKpTzL.domain.model.AppSettings
import com.FqJvXmR.nKpTzL.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val preferences: SettingsPreferences) : SettingsRepository {

    override fun settings(): AppSettings = AppSettings(
        soundCues = preferences.soundCues(),
        haptics = preferences.haptics(),
        highContrast = preferences.highContrast()
    )

    override fun update(settings: AppSettings) {
        preferences.store(settings.soundCues, settings.haptics, settings.highContrast)
    }
}
