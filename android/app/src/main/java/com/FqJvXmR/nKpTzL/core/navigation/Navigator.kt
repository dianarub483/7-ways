package com.FqJvXmR.nKpTzL.core.navigation

import androidx.fragment.app.FragmentManager
import com.FqJvXmR.nKpTzL.R
import com.FqJvXmR.nKpTzL.domain.model.RoundResult
import com.FqJvXmR.nKpTzL.presentation.game.GameFragment
import com.FqJvXmR.nKpTzL.presentation.gameover.GameOverFragment
import com.FqJvXmR.nKpTzL.presentation.menu.MenuFragment
import com.FqJvXmR.nKpTzL.presentation.splash.SplashFragment

class Navigator(
    private val fragmentManager: FragmentManager,
    private val containerId: Int
) {

    fun showSplash() {
        if (fragmentManager.isStateSaved) {
            return
        }
        fragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(containerId, SplashFragment())
            .commit()
    }

    fun showMenu(): Boolean {
        if (fragmentManager.isStateSaved) {
            return false
        }
        fragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(containerId, MenuFragment())
            .commit()
        return true
    }

    fun showGame(levelIndex: Int) {
        if (fragmentManager.isStateSaved) {
            return
        }
        fragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out_left
            )
            .replace(containerId, GameFragment.newInstance(levelIndex))
            .addToBackStack(TAG_GAME)
            .commit()
    }

    fun showResult(result: RoundResult) {
        if (fragmentManager.isStateSaved) {
            return
        }
        fragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.scale_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            )
            .replace(containerId, GameOverFragment.newInstance(result))
            .addToBackStack(TAG_RESULT)
            .commit()
    }

    fun backToMenu() {
        if (fragmentManager.isStateSaved) {
            return
        }
        fragmentManager.popBackStack(TAG_GAME, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    fun restartGame(levelIndex: Int) {
        backToMenu()
        showGame(levelIndex)
    }

    private companion object {
        const val TAG_GAME = "game"
        const val TAG_RESULT = "result"
    }
}
