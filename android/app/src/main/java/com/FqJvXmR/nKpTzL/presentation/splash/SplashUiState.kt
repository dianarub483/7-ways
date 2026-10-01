package com.FqJvXmR.nKpTzL.presentation.splash

data class SplashUiState(
    val elapsedMs: Long,
    val totalMs: Long,
    val handOffReady: Boolean
) {
    val fraction: Float
        get() = if (totalMs <= 0L) 1f else (elapsedMs.toFloat() / totalMs.toFloat()).coerceIn(0f, 1f)

    val percent: Int
        get() = (fraction * 100f).toInt()
}
