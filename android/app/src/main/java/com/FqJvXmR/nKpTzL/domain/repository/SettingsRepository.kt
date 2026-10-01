package com.FqJvXmR.nKpTzL.domain.repository

import com.FqJvXmR.nKpTzL.domain.model.AppSettings

interface SettingsRepository {
    fun settings(): AppSettings
    fun update(settings: AppSettings)
}
