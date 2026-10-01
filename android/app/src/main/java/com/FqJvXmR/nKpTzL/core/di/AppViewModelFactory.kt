package com.FqJvXmR.nKpTzL.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.FqJvXmR.nKpTzL.presentation.game.GameViewModel
import com.FqJvXmR.nKpTzL.presentation.gameover.GameOverViewModel
import com.FqJvXmR.nKpTzL.presentation.menu.MenuViewModel
import com.FqJvXmR.nKpTzL.presentation.splash.SplashViewModel

class AppViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val created: ViewModel = when (modelClass) {
            SplashViewModel::class.java -> SplashViewModel()
            MenuViewModel::class.java -> MenuViewModel(
                ServiceLocator.getProgressUseCase(),
                ServiceLocator.levelRepository()
            )
            GameViewModel::class.java -> GameViewModel(
                ServiceLocator.generateLevelUseCase(),
                ServiceLocator.rotateCellUseCase(),
                ServiceLocator.traceBeamsUseCase(),
                ServiceLocator.evaluateRoundUseCase(),
                ServiceLocator.saveProgressUseCase(),
                ServiceLocator.progressRepository(),
                ServiceLocator.levelRepository()
            )
            GameOverViewModel::class.java -> GameOverViewModel(ServiceLocator.levelRepository())
            else -> throw IllegalArgumentException("Unknown ViewModel requested")
        }
        return created as T
    }
}
