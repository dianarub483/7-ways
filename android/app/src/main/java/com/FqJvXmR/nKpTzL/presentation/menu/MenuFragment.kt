package com.FqJvXmR.nKpTzL.presentation.menu

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.FqJvXmR.nKpTzL.MainActivity
import com.FqJvXmR.nKpTzL.R
import com.FqJvXmR.nKpTzL.core.di.AppViewModelFactory
import com.FqJvXmR.nKpTzL.databinding.FragmentMenuBinding
import com.FqJvXmR.nKpTzL.presentation.dialog.LevelMapDialog
import com.FqJvXmR.nKpTzL.presentation.dialog.SettingsDialog
import com.FqJvXmR.nKpTzL.presentation.dialog.TutorialDialog
import kotlinx.coroutines.launch

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding

    private val lampAnimators = mutableListOf<ObjectAnimator>()

    private val viewModel: MenuViewModel by viewModels { AppViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflated = FragmentMenuBinding.inflate(inflater, container, false)
        _binding = inflated
        return inflated.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val views = binding ?: return

        views.menuPlay.setOnClickListener {
            val host = activity as? MainActivity ?: return@setOnClickListener
            host.navigator.showGame(viewModel.uiState.value.currentLevel)
        }
        views.menuMap.setOnClickListener {
            LevelMapDialog().show(parentFragmentManager, TAG_MAP)
        }
        views.menuHowto.setOnClickListener {
            TutorialDialog().show(parentFragmentManager, TAG_TUTORIAL)
        }
        views.menuSettings.setOnClickListener {
            SettingsDialog().show(parentFragmentManager, TAG_SETTINGS)
        }

        playEntrance(views)
        startLampPulse(views)
        observeState()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    private fun playEntrance(views: FragmentMenuBinding) {
        val density = resources.displayMetrics.density
        views.menuSheet.translationY = SHEET_OFFSET_DP * density
        views.menuSheet.alpha = 0f
        views.menuSheet.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(SHEET_IN_MS)
            .setInterpolator(DecelerateInterpolator())
            .start()

        views.menuPlay.scaleX = CTA_START_SCALE
        views.menuPlay.scaleY = CTA_START_SCALE
        views.menuPlay.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setStartDelay(CTA_DELAY_MS)
            .setDuration(CTA_IN_MS)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    private fun startLampPulse(views: FragmentMenuBinding) {
        val lamps = listOf(
            views.lamp1,
            views.lamp2,
            views.lamp3,
            views.lamp4,
            views.lamp5,
            views.lamp6,
            views.lamp7
        )
        lamps.forEachIndexed { index, lamp ->
            val pulse = ObjectAnimator.ofFloat(lamp, View.ALPHA, LAMP_MIN_ALPHA, 1f)
            pulse.duration = LAMP_MS
            pulse.startDelay = index * LAMP_STEP_MS
            pulse.repeatCount = LAMP_REPEATS
            pulse.repeatMode = ValueAnimator.REVERSE
            pulse.interpolator = AccelerateDecelerateInterpolator()
            pulse.start()
            lampAnimators.add(pulse)
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    render(state)
                }
            }
        }
    }

    private fun render(state: MenuUiState) {
        val views = binding ?: return
        if (state.hasProgress) {
            views.menuStatsRow.visibility = View.VISIBLE
            views.menuStatBest.bind(
                getString(R.string.stat_best),
                state.bestScore.toString()
            )
            views.menuStatCleared.bind(
                getString(R.string.stat_cleared),
                getString(R.string.count_fmt, state.levelsCleared, state.totalLevels)
            )
        } else {
            views.menuStatsRow.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        lampAnimators.forEach { animator ->
            animator.cancel()
        }
        lampAnimators.clear()
        binding?.menuSheet?.animate()?.cancel()
        binding?.menuPlay?.animate()?.cancel()
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val TAG_MAP = "level_map"
        const val TAG_TUTORIAL = "tutorial"
        const val TAG_SETTINGS = "settings"
        const val SHEET_OFFSET_DP = 56f
        const val SHEET_IN_MS = 420L
        const val CTA_START_SCALE = 0.94f
        const val CTA_DELAY_MS = 180L
        const val CTA_IN_MS = 260L
        const val LAMP_MIN_ALPHA = 0.35f
        const val LAMP_MS = 600L
        const val LAMP_STEP_MS = 90L
        const val LAMP_REPEATS = 8
    }
}
