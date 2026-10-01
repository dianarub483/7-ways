package com.FqJvXmR.nKpTzL.core.di

import android.content.Context
import com.FqJvXmR.nKpTzL.data.local.ProgressPreferences
import com.FqJvXmR.nKpTzL.data.local.SettingsPreferences
import com.FqJvXmR.nKpTzL.data.repository.LevelRepositoryImpl
import com.FqJvXmR.nKpTzL.data.repository.ProgressRepositoryImpl
import com.FqJvXmR.nKpTzL.data.repository.SettingsRepositoryImpl
import com.FqJvXmR.nKpTzL.domain.repository.LevelRepository
import com.FqJvXmR.nKpTzL.domain.repository.ProgressRepository
import com.FqJvXmR.nKpTzL.domain.repository.SettingsRepository
import com.FqJvXmR.nKpTzL.domain.usecase.EvaluateRoundUseCase
import com.FqJvXmR.nKpTzL.domain.usecase.GenerateLevelUseCase
import com.FqJvXmR.nKpTzL.domain.usecase.GetProgressUseCase
import com.FqJvXmR.nKpTzL.domain.usecase.RotateCellUseCase
import com.FqJvXmR.nKpTzL.domain.usecase.SaveProgressUseCase
import com.FqJvXmR.nKpTzL.domain.usecase.TraceBeamsUseCase

object ServiceLocator {

    private var appContext: Context? = null

    private var levelRepositoryRef: LevelRepository? = null
    private var progressRepositoryRef: ProgressRepository? = null
    private var settingsRepositoryRef: SettingsRepository? = null

    fun init(context: Context) {
        if (appContext != null) {
            return
        }
        val application = context.applicationContext
        appContext = application
        val levels = LevelRepositoryImpl()
        levelRepositoryRef = levels
        progressRepositoryRef = ProgressRepositoryImpl(ProgressPreferences(application), levels)
        settingsRepositoryRef = SettingsRepositoryImpl(SettingsPreferences(application))
    }

    fun levelRepository(): LevelRepository {
        val existing = levelRepositoryRef
        if (existing != null) {
            return existing
        }
        val created = LevelRepositoryImpl()
        levelRepositoryRef = created
        return created
    }

    fun progressRepository(): ProgressRepository {
        val existing = progressRepositoryRef
        if (existing != null) {
            return existing
        }
        val context = appContext
        val created = if (context != null) {
            ProgressRepositoryImpl(ProgressPreferences(context), levelRepository())
        } else {
            throw IllegalStateException("ServiceLocator was not initialised")
        }
        progressRepositoryRef = created
        return created
    }

    fun settingsRepository(): SettingsRepository {
        val existing = settingsRepositoryRef
        if (existing != null) {
            return existing
        }
        val context = appContext
        val created = if (context != null) {
            SettingsRepositoryImpl(SettingsPreferences(context))
        } else {
            throw IllegalStateException("ServiceLocator was not initialised")
        }
        settingsRepositoryRef = created
        return created
    }

    fun generateLevelUseCase(): GenerateLevelUseCase = GenerateLevelUseCase(levelRepository())

    fun rotateCellUseCase(): RotateCellUseCase = RotateCellUseCase()

    fun traceBeamsUseCase(): TraceBeamsUseCase = TraceBeamsUseCase()

    fun evaluateRoundUseCase(): EvaluateRoundUseCase = EvaluateRoundUseCase()

    fun getProgressUseCase(): GetProgressUseCase = GetProgressUseCase(progressRepository())

    fun saveProgressUseCase(): SaveProgressUseCase = SaveProgressUseCase(progressRepository())
}
