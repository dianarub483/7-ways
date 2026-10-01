package com.FqJvXmR.nKpTzL.presentation.splash

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.FqJvXmR.nKpTzL.core.config.GameConfig

class SplashAnimator(
    private val emblem: View,
    private val glow: View,
    private val title: View,
    private val subtitle: View,
    private val loadingLabel: View,
    private val rays: List<View>
) {

    private val running = mutableListOf<ObjectAnimator>()
    private var started = false

    fun prepare() {
        emblem.alpha = 0f
        emblem.scaleX = ENTRY_SCALE
        emblem.scaleY = ENTRY_SCALE
        glow.alpha = 0f
        title.alpha = 0f
        title.translationY = TITLE_OFFSET_DP * title.resources.displayMetrics.density
        subtitle.alpha = 0f
        loadingLabel.alpha = LOADING_MIN_ALPHA
        rays.forEach { ray ->
            ray.alpha = 0f
            ray.scaleX = 0f
        }
    }

    fun start() {
        if (started) {
            return
        }
        started = true
        animateEmblem()
        animateGlow()
        animateTitle()
        animateRays()
        animateLoadingLabel()
    }

    private fun animateEmblem() {
        emblem.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(EMBLEM_IN_MS)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction {
                try {
                    if (emblem.isAttachedToWindow) {
                        startEmblemPulse()
                    }
                } catch (e: Exception) {
                    started = true
                }
            }
            .start()
    }

    private fun startEmblemPulse() {
        val pulseX = ObjectAnimator.ofFloat(emblem, View.SCALE_X, 1f, PULSE_SCALE)
        val pulseY = ObjectAnimator.ofFloat(emblem, View.SCALE_Y, 1f, PULSE_SCALE)
        listOf(pulseX, pulseY).forEach { animator ->
            animator.duration = PULSE_MS
            animator.repeatCount = ValueAnimator.INFINITE
            animator.repeatMode = ValueAnimator.REVERSE
            animator.interpolator = AccelerateDecelerateInterpolator()
            animator.start()
            running.add(animator)
        }
    }

    private fun animateGlow() {
        val fade = ObjectAnimator.ofFloat(glow, View.ALPHA, 0f, 1f)
        fade.duration = GLOW_IN_MS
        fade.interpolator = DecelerateInterpolator()
        fade.start()
        running.add(fade)

        val spin = ObjectAnimator.ofFloat(glow, View.ROTATION, 0f, FULL_TURN)
        spin.duration = GLOW_SPIN_MS
        spin.repeatCount = ValueAnimator.INFINITE
        spin.repeatMode = ValueAnimator.RESTART
        spin.interpolator = LinearInterpolator()
        spin.start()
        running.add(spin)
    }

    private fun animateTitle() {
        title.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(TITLE_DELAY_MS)
            .setDuration(TITLE_IN_MS)
            .setInterpolator(DecelerateInterpolator())
            .start()

        subtitle.animate()
            .alpha(1f)
            .setStartDelay(SUBTITLE_DELAY_MS)
            .setDuration(TITLE_IN_MS)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    private fun animateRays() {
        rays.forEachIndexed { index, ray ->
            ray.animate()
                .alpha(1f)
                .scaleX(1f)
                .setStartDelay(GameConfig.SPLASH_RAY_STEP_MS * index)
                .setDuration(RAY_IN_MS)
                .setInterpolator(DecelerateInterpolator())
                .withEndAction {
                    try {
                        if (ray.isAttachedToWindow) {
                            breatheRay(ray, index)
                        }
                    } catch (e: Exception) {
                        ray.alpha = 1f
                    }
                }
                .start()
        }
    }

    private fun breatheRay(ray: View, index: Int) {
        val breathe = ObjectAnimator.ofFloat(ray, View.ALPHA, RAY_MIN_ALPHA, 1f)
        breathe.duration = RAY_BREATHE_MS
        breathe.startDelay = index * RAY_PHASE_MS
        breathe.repeatCount = ValueAnimator.INFINITE
        breathe.repeatMode = ValueAnimator.REVERSE
        breathe.interpolator = AccelerateDecelerateInterpolator()
        breathe.start()
        running.add(breathe)
    }

    private fun animateLoadingLabel() {
        val blink = ObjectAnimator.ofFloat(loadingLabel, View.ALPHA, LOADING_MIN_ALPHA, 1f)
        blink.duration = LOADING_BLINK_MS
        blink.repeatCount = ValueAnimator.INFINITE
        blink.repeatMode = ValueAnimator.REVERSE
        blink.interpolator = AccelerateDecelerateInterpolator()
        blink.start()
        running.add(blink)
    }

    fun cancel() {
        running.forEach { animator ->
            animator.cancel()
        }
        running.clear()
        emblem.animate().cancel()
        glow.animate().cancel()
        title.animate().cancel()
        subtitle.animate().cancel()
        loadingLabel.animate().cancel()
        rays.forEach { ray ->
            ray.animate().cancel()
        }
        started = false
    }

    private companion object {
        const val ENTRY_SCALE = 0.82f
        const val PULSE_SCALE = 1.045f
        const val FULL_TURN = 360f
        const val TITLE_OFFSET_DP = 24f
        const val EMBLEM_IN_MS = 560L
        const val GLOW_IN_MS = 720L
        const val GLOW_SPIN_MS = 9000L
        const val PULSE_MS = 1400L
        const val TITLE_DELAY_MS = 220L
        const val SUBTITLE_DELAY_MS = 420L
        const val TITLE_IN_MS = 480L
        const val RAY_IN_MS = 360L
        const val RAY_BREATHE_MS = 900L
        const val RAY_PHASE_MS = 80L
        const val RAY_MIN_ALPHA = 0.35f
        const val LOADING_BLINK_MS = 820L
        const val LOADING_MIN_ALPHA = 0.45f
    }
}
